import { configureStore } from "@reduxjs/toolkit";
import {render, RenderOptions} from "@testing-library/react";
import { ToDoState } from "../types/todoTypes";
import todoReducer from "../redux/slices/todoSlice";
import { Provider } from "react-redux";

type RequestStatus = ToDoState['status'];

// Redux store with optional preloaded staste
const createTestStore = (preloadedState: Partial<{todos: ToDoState}> = {}) => {
    return configureStore({
        reducer: {
            todos: todoReducer
        },
        preloadedState: {
            todos: {
                items: [],
                totalCount: 0,
                stats: null,
                status: 'idle' as RequestStatus,
                error: null,
                pagination: {currentPage: 0, pageSize: 10, totalPages: 0, isLast: true},
                sortBy: [],
                filterBy: null,
                ...preloadedState?.todos,
            }
        }
    })
}

// Wrapper for rendering components with the Redux store
export const renderWithRedux = (
    ui: React.ReactElement,
    preloadedState: Partial<{ todos: ToDoState }> = {},
    renderOptions: RenderOptions = {}
) => {
    const store = createTestStore(preloadedState);
    const Wrapper: React.FC<{ children: React.ReactNode }> = ({ children }) => (
        <Provider store={store}>{children}</Provider>
    );

    return { store, ...render(ui, { wrapper: Wrapper, ...renderOptions }) };
}; 