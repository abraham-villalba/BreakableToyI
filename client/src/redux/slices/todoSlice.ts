import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Sort, ToDo, ToDoFilter, ToDoFormForApi, ToDoState } from "../../types/todoTypes";
import { completeTodo, createTodo, deleteTodo, getStats, getTodos, uncompleteTodo, updateTodo } from "../../api/todosApi";
import { AppDispatch, RootState } from "../store";
import { AxiosResponse, HttpStatusCode } from "axios";

/**
 * Auxiliary function that checks if the status is idle
 * 
 * @param status {ToDoState['status']} status of the request
 * @returns {boolean} true if the status is idle
 */
const isIdle = (status: ToDoState['status']) => status === 'idle'; 

/**
 * Builds the query string for the To Do list
 * 
 * @param page {number} current page
 * @param sort {Sort[]} sorting options
 * @param filters {ToDoFilter | null} filtering options
 * @returns {string} query string
 */
const buildUrlQuery = (page: number, sort: Sort[], filters: ToDoFilter | null): string => {
    let query = "?";
    query += `page=${page}`;
    if (sort.length > 0) {
        query += '&sortBy=';
        sort.map((item) => {
            query += `${item.field}:${item.asc ? "asc" : "desc"},`;
        })
        // Remove the last coma
        query = query.slice(0, -1);
    }
    if (filters) {
        const filterEntries = Object.entries(filters);
        filterEntries.forEach(([key, value]) => {
            if (value !== null) {
                query += `&${key}=${value}`;
            }
        });
    }
    
    return query;
}  

/**
 * Fetches To Dos
 */
export const fetchToDos = createAsyncThunk(
    'todos/fetchTodos',
    async (_, thunkApi) => {
        try {
            const state = thunkApi.getState() as RootState;
            const queryParameters = buildUrlQuery(state.todos.pagination.currentPage, state.todos.sortBy, state.todos.filterBy);
            const response = await getTodos(queryParameters);
            return response;
        } catch (error) {
            console.log(error);
            return thunkApi.rejectWithValue({message: error instanceof Error ? error.message : "Unable to fetch records..."});
        }
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

/**
 * Updates a To Do
 */
export const updateToDo = createAsyncThunk(
    'todos/updateTodo',
    async (data : {id: string, todoForm: ToDoFormForApi}, {rejectWithValue}) => {
        try {
            const {id, todoForm} = data;
            const dataForApi = {
                text: todoForm.text,
                priority: todoForm.priority,
                dueDate: todoForm.dueDate !== "" ? todoForm.dueDate : null
            };
            const response = await updateTodo(id, dataForApi);
            return response;
        } catch (error) {
            console.log(error);
            return rejectWithValue({message: error instanceof Error ? error.message : "Error updating To Do"});
        }
        
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

/**
 * Toggles a To Do (Complete/Uncomplete)
 */
export const toggleTodo = createAsyncThunk(
    'todo/toggleTodo',
    async (todo: ToDo, {rejectWithValue}) => {
        try {
            const {id, done} = todo;
            let response: AxiosResponse<any, any>;
            if (done) {
                response = await uncompleteTodo(id);
            } else {
                response = await completeTodo(id);
            }
            return response;
        } catch (error) {
            return rejectWithValue({message: error instanceof Error ? error.message : "Error completing/uncompleting To Do"});
        }
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

/**
 * Removes a To Do (Delete)
 */
export const removeTodo = createAsyncThunk(
    'todo/removeTodo',
    async (id: string, {rejectWithValue}) => {
        try {
            const status = await deleteTodo(id);
            if (status !== HttpStatusCode.NoContent) {
                throw new Error("Failed to delete task...")
            }
            return id;
        } catch (error) {
            return rejectWithValue({message: error instanceof Error ? error.message : "Error deleting To Do"});
        }
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

/**
 * Creates a new To Do
 */
export const createToDo = createAsyncThunk(
    'todos/createTodo',
    async (todoForm: ToDoFormForApi, {rejectWithValue}) => {
        try {
            const dataForApi = {
                text: todoForm.text,
                priority: todoForm.priority,
                dueDate: todoForm.dueDate !== "" ? todoForm.dueDate : null
            };
            const response = await createTodo(dataForApi);
            return response;
        } catch (error) {
            return rejectWithValue({message: error instanceof Error ? error.message : "Error creating To Do"});
        }
        
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

/**
 * Fetches To Do statistics
 */
export const fetchStats = createAsyncThunk(
    'todos/fetchStats',
    async (_, {rejectWithValue}) => {
        try {
            const response = await getStats();
            return response;
        } catch (error) {
            console.log(error);
            return rejectWithValue({message: error instanceof Error ? error.message : "Error fetching To Do statistics"});
        }  
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

// Utility function to dispatch an action and then fetch the statistics
const dispatchActionAndFetchStats = async (dispatch: AppDispatch, action: any) => {
    try {
        await dispatch(action);
        await dispatch(fetchStats());
    } catch (error) {
        console.error('Error dispatching action and fetching stats:', error);
    }
}

/**
 * Fetches To Dos and statistics
 * 
 * @returns {Promise<void>}
 */
export const fetchToDosAndStats = () => async (dispatch : AppDispatch) => {
    await dispatchActionAndFetchStats(dispatch, fetchToDos());
}

/**
 * Delete a ToDo and updates the statistics
 * 
 * @param id {string} id of the ToDo to delete
 * @returns {Promise<void>}
 */
export const deleteToDoAndUpdateStats = (id: string) => async (dispatch : AppDispatch) => {
    await dispatchActionAndFetchStats(dispatch, removeTodo(id));
}

/**
 * Update a ToDo and updates the statistics
 * 
 * @param data {id: string, todoForm: ToDoFormForApi} id of the ToDo to update and the new data
 * @returns {Promise<void>}
 */
export const updateToDoAndStats = (data : {id: string, todoForm: ToDoFormForApi}) => async (dispatch : AppDispatch) => {
    await dispatchActionAndFetchStats(dispatch, updateToDo(data));
}

/**
 * Toggle a ToDo and update the statistics
 * 
 * @param todo ToDo to toggle
 * @returns  {Promise<void>} 
 */
export const toggleToDoAndUpdateStats = (todo: ToDo) => async (dispatch : AppDispatch) => {
    await dispatchActionAndFetchStats(dispatch, toggleTodo(todo));
}

// Slice initial state
const initialState: ToDoState = {
    items: [],
    totalCount: 0,
    stats: null,
    status: 'idle',
    error: null,
    pagination: {
        currentPage: 0,
        pageSize: 10,
        totalPages: 0,
        isLast: true
    },
    sortBy: [],
    filterBy: null
}

/**
 * todoSlice
 * 
 * This slice manages the state of the To Do list, including the list of To Dos, 
 * the current page, the sorting and filtering options, and the statistics.
 * It includes actions for fetching To Dos, updating To Dos, toggling To Dos,
 * removing To Dos, creating To Dos, and fetching statistics.
 * 
 * @reduxSlice
 */
const todoSlice = createSlice({
    name: 'todos',
    initialState,
    reducers: {
        // Set the current page
        setCurrentPage(state, action: PayloadAction<number>) {
            state.pagination.currentPage = action.payload;
        },
        // Update an item in the list
        updateItem(state, action: PayloadAction<ToDo>) {
            // Only update the todo if it's on the current items list.
            const updatedToDo: ToDo = action.payload;
            const index = state.items.findIndex((todo) => todo.id === updatedToDo.id);
            if (index >= 0) {
                state.items[index] = updatedToDo;
            }
        },
        // Remove an item from the list
        removeItemWithId(state, action: PayloadAction<string>) {
            state.items = state.items.filter(item => item.id !== action.payload);
            state.totalCount--;
        },
        // Insert a new item to the list
        insertItem(state, action: PayloadAction<any>) {
            state.items.unshift(action.payload);
            if (state.items.length > 10) {
                state.items.pop();
            } else {
                state.totalCount++;
                state.pagination.totalPages = state.pagination.totalPages > 0 ? state.pagination.totalPages : 1;
            }
        },
        // Add a sorting option to the list
        addSortBy(state, action: PayloadAction<string>) {

            const field = action.payload;
            const sortBy = state.sortBy.map((item) => 
                item.field === field ? { ...item, asc: !item.asc } : item
            );

            const exists = state.sortBy.some((item) => item.field === field);
            state.sortBy = exists ? sortBy : [...sortBy, {field, asc: true}];
        },
        // Add a filter to the list
        addFilterBy(state, action: PayloadAction<ToDoFilter>) {
            const filter = action.payload;
            state.filterBy = filter;
        },
        // Set the statistics
        setStats(state, action: PayloadAction<any>) {
            const stats : ToDoState['stats'] = {
                completed: action.payload.totalDone,
                completedAvgTime: action.payload.averageDoneTime,
                completedHigh: action.payload.totalHighDone,
                completedHighAvgTime: action.payload.averageHighDoneTime,
                completedLow: action.payload.totalLowDone,
                completedLowAvgTime: action.payload.averageLowDoneTime,
                completedMedium: action.payload.totalMediumDone,
                completedMediumAvgTime: action.payload.averageMediumDoneTime
            };
            state.stats  = stats;
        },
        // Clear the error message
        clearError(state) {
            state.error = null;
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchToDos.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(fetchToDos.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'succeded';
                state.items = action.payload.content;
                state.totalCount = action.payload.totalElements;
                state.pagination.totalPages = action.payload.totalPages;
                state.pagination.isLast = action.payload.last;
                state.status = 'idle';
            })
            .addCase(fetchToDos.rejected, (state, action: PayloadAction<any>) => {
                state.status = 'failed';
                state.error = action.payload.error ?? 'Unknown error';
                state.status = 'idle';
            })
            .addCase(updateToDo.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(updateToDo.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                todoSlice.caseReducers.updateItem(state, action);
            })
            .addCase(updateToDo.rejected,(state, action: PayloadAction<any>) => {
                state.status = 'failed';
                state.error = action.payload.message ?? 'Unknown error';
                state.status = 'idle';
            })
            .addCase(toggleTodo.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(toggleTodo.rejected, (state, action: PayloadAction<any>) => {
                state.status = 'failed';
                state.status = 'idle';
                state.error = action.payload.message ?? 'Unknown error';
            })
            .addCase(toggleTodo.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                todoSlice.caseReducers.updateItem(state, action);
            })
            .addCase(removeTodo.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(removeTodo.rejected, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                state.error = action.payload.message ?? 'Unknown error';
            })
            .addCase(removeTodo.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                todoSlice.caseReducers.removeItemWithId(state, action);
            })
            .addCase(createToDo.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(createToDo.rejected, (state, action: PayloadAction<any>) => {
                state.error = action.payload.message ?? 'Unknown error';
                state.status = 'idle';
            })
            .addCase(createToDo.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                todoSlice.caseReducers.insertItem(state, action);
            })
            .addCase(fetchStats.pending, (state) => {
                state.status = 'loading'; // Stats request in progress
            })
            .addCase(fetchStats.fulfilled, (state, action: PayloadAction<any>) => {
                todoSlice.caseReducers.setStats(state, action);
                state.status = 'idle';
            })
            .addCase(fetchStats.rejected, (state, action: PayloadAction<any>) => {
                state.error = action.payload.message ?? 'Unknown error';
                state.status = 'idle';
            })
    }
})

export const selectTodosRequestStatus = (state: RootState) => state.todos.status;
export const { setCurrentPage, addSortBy, addFilterBy, clearError, insertItem, updateItem, removeItemWithId, setStats } = todoSlice.actions;
export default todoSlice.reducer;