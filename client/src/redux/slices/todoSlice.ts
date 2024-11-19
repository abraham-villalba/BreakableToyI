import { createAsyncThunk, createSlice, PayloadAction } from "@reduxjs/toolkit";
import { ToDoState } from "../../types/todoTypes";
import { getTodos } from "../../api/todosApi";



export const fetchToDos = createAsyncThunk(
    'todos/fetchTodos',
    async () => {
        const response = await getTodos("");
        return response.data;
    }
);

const initialState: ToDoState = {
    items: [],
    totalCount: 0,
    stats: null,
    status: 'idle',
    error: null
}

const todoSlice = createSlice({
    name: 'todos',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(fetchToDos.pending, (state) => {
                state.status = 'loading';
            })
            .addCase(fetchToDos.fulfilled, (state, action: PayloadAction<any>) => {
                state.status = 'succeded';
                state.items = action.payload.content;
                state.totalCount = action.payload.totalElements;
            })
            .addCase(fetchToDos.rejected, (state, action: PayloadAction<any>) => {
                state.status = 'failed';
                state.error = action.payload.error ?? 'Unknown error';
            })
    }
})

export default todoSlice.reducer;