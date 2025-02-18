/**
 * @file This file contains the configuration for the Redux store.
 */

import { configureStore } from "@reduxjs/toolkit";
import todosReducer from './slices/todoSlice';

/**
 * The Redux store for the application.
 * 
 * This function sets up the Redux store with the necessary reducers and middleware.
 * 
 * @returns {Store} - The configured Redux store.
 */
export const store = configureStore({
    reducer: {
        todos: todosReducer
    }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;