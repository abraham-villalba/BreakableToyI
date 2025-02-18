import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { Sort, ToDo, ToDoFilter, ToDoFormForApi, ToDoState } from "../../types/todoTypes";
import { completeTodo, createTodo, deleteTodo, getStats, getTodos, uncompleteTodo, updateTodo } from "../../api/todosApi";
import { AppDispatch, RootState } from "../store";
import { AxiosResponse, HttpStatusCode } from "axios";

// Debbuging

const isIdle = (status: ToDoState['status']) => status === 'idle'; 

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

export const fetchToDos = createAsyncThunk(
    'todos/fetchTodos',
    async (_, thunkApi) => {
        try {
            const state = thunkApi.getState() as RootState;
            const queryParameters = buildUrlQuery(state.todos.pagination.currentPage, state.todos.sortBy, state.todos.filterBy);
            const response = await getTodos(queryParameters);
            return response.data;
        } catch (error) {
            return thunkApi.rejectWithValue({message: "Unable to fetch records..."});
        }
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

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
            return response.data;
        } catch (error) {
            console.log(error);
            return rejectWithValue({message: "Error updating To Do"});
        }
        
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

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
            return response.data;
        } catch (error) {
            return rejectWithValue({message: "Error completing/uncompleting To Do"});
        }
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

export const removeTodo = createAsyncThunk(
    'todo/removeTodo',
    async (id: string, {rejectWithValue}) => {
        try {
            const response = await deleteTodo(id);
            if (response.status !== HttpStatusCode.Ok) {
                throw new Error("Failed to delete task...")
            }
            return id;
        } catch (error) {
            return rejectWithValue({message: "Error deleting To Do"});
        }
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

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
            return response.data;
        } catch (error) {
            return rejectWithValue("Error creating To Do");
        }
        
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

export const fetchStats = createAsyncThunk(
    'todos/fetchStats',
    async (_, {rejectWithValue}) => {
        try {
            const response = await getStats();
            return response.data;
        } catch (error) {
            return rejectWithValue({message: "Error fetching To Do statistics"});
        }  
    },
    {
        condition(_, thunkApi) {
            return isIdle(selectTodosRequestStatus(thunkApi.getState() as RootState));
        }
    }
);

export const fetchToDosAndStats = () => async (dispatch : AppDispatch) => {
    await dispatch(fetchToDos());
    await dispatch(fetchStats());
}

export const deleteToDoAndUpdateStats = (id: string) => async (dispatch : AppDispatch) => {
    await dispatch(removeTodo(id));
    await dispatch(fetchStats());
}

export const updateToDoAndStats = (data : {id: string, todoForm: ToDoFormForApi}) => async (dispatch : AppDispatch) => {
    await dispatch(updateToDo(data));
    await dispatch(fetchStats());
}

export const toggleToDoAndUpdateStats = (todo: ToDo) => async (dispatch : AppDispatch) => {
    await dispatch(toggleTodo(todo));
    await dispatch(fetchStats());
}

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

const todoSlice = createSlice({
    name: 'todos',
    initialState,
    reducers: {
        setCurrentPage(state, action: PayloadAction<number>) {
            state.pagination.currentPage = action.payload;
        },
        updateItem(state, action: PayloadAction<ToDo>) {
            // Only update the todo if it's on the current items list.
            const updatedToDo: ToDo = action.payload;
            console.log(updateToDo);
            const index = state.items.findIndex((todo) => todo.id === updatedToDo.id);
            if (index >= 0) {
                state.items[index] = updatedToDo;
            }
        },
        removeItemWithId(state, action: PayloadAction<string>) {
            state.items = state.items.filter(item => item.id !== action.payload);
            state.totalCount--;
        },
        insertItem(state, action: PayloadAction<any>) {
            state.items.unshift(action.payload);
            if (state.items.length > 10) {
                state.items.pop();
            } else {
                state.totalCount++;
                state.pagination.totalPages = state.pagination.totalPages > 0 ? state.pagination.totalPages : 1;
            }
        },
        addSortBy(state, action: PayloadAction<string>) {

            const field = action.payload;
            const sortBy = state.sortBy.map((item) => 
                item.field === field ? { ...item, asc: !item.asc } : item
            );

            const exists = state.sortBy.some((item) => item.field === field);
            state.sortBy = exists ? sortBy : [...sortBy, {field, asc: true}];
        },
        addFilterBy(state, action: PayloadAction<ToDoFilter>) {
            const filter = action.payload;
            state.filterBy = filter;
        },
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
                state.error = action.payload.message ?? 'Unknown error';
            })
            .addCase(toggleTodo.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                todoSlice.caseReducers.updateItem(state, action);
            })
            .addCase(removeTodo.pending, (state) => {
                state.status = 'idle';
            })
            .addCase(removeTodo.rejected, (state, action: PayloadAction<any>) => {
                state.error = action.payload.message ?? 'Unknown error';
            })
            .addCase(removeTodo.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                todoSlice.caseReducers.removeItemWithId(state, action);
            })
            .addCase(createToDo.pending, (state) => {
                state.status = 'idle';
            })
            .addCase(createToDo.rejected, (state, action: PayloadAction<any>) => {
                state.error = action.payload.message ?? 'Unknown error';
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
            })
    }
})

export const selectTodosRequestStatus = (state: RootState) => state.todos.status;
export const { setCurrentPage, addSortBy, addFilterBy, clearError, insertItem, updateItem, removeItemWithId, setStats } = todoSlice.actions;
export default todoSlice.reducer;