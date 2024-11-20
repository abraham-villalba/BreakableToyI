import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ToDo, ToDoFormForApi, ToDoState } from "../../types/todoTypes";
import { getTodos, updateTodo } from "../../api/todosApi";
import { RootState } from "../store";



export const fetchToDos = createAsyncThunk(
    'todos/fetchTodos',
    async (_, thunkApi) => {
        const state = thunkApi.getState() as RootState;
        const page = state.todos.pagination.currentPage;
        let queryParameters = "?page=" + page;
        console.log(queryParameters);
        const response = await getTodos(queryParameters);
        return response.data;
    },
    {
        condition(_, thunkApi) {
            const todosStatus = selectTodosRequestStatus(thunkApi.getState() as RootState);
            if (todosStatus !== 'idle') {
                return false; // Prevent from fetching data if there's fetching currently.
            }
        }
    }
);

export const updateToDo = createAsyncThunk(
    'todos/updateTodo',
    async (data : {id: string, todoForm: ToDoFormForApi}) => {
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
        }
        
    },
    {
        condition(_, thunkApi) {
            const todosStatus = selectTodosRequestStatus(thunkApi.getState() as RootState);
            if (todosStatus !== 'idle') {
                return false; // Prevent from sending a request if there's another request active currently.
            }
        }
    }
);

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
    }
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
        }
    },
    extraReducers: (builder) => {
        builder
            .addCase(fetchToDos.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(fetchToDos.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'idle';
                state.items = action.payload.content;
                state.totalCount = action.payload.totalElements;
                state.pagination.totalPages = action.payload.totalPages;
                state.pagination.isLast = action.payload.last;
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
                console.log(action);
                todoSlice.caseReducers.updateItem(state, action);
            })
            .addCase(updateToDo.rejected,(state, action: PayloadAction<any>) => {
                state.status = 'failed';
                //state.error = action.payload.error ?? 'Unknown error';
                console.log("Error updating todo");
                console.log(action);
                state.status = 'idle';
            })
    }
})

export const selectTodosRequestStatus = (state: RootState) => state.todos.status;
export const { setCurrentPage } = todoSlice.actions;
export default todoSlice.reducer;