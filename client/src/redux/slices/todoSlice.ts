import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ToDoState } from "../../types/todoTypes";
import { getTodos } from "../../api/todosApi";
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
            console.log("Updated page to" + action.payload);
            console.log("final is " + state.pagination.isLast);
            console.log("pages " + state.pagination.totalPages);
            state.pagination.currentPage = action.payload;
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
            })
    }
})

export const selectTodosRequestStatus = (state: RootState) => state.todos.status;
export const { setCurrentPage } = todoSlice.actions;
export default todoSlice.reducer;