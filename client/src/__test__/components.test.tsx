import { describe, expect, it } from "vitest";
import { ToDoState } from "../types/todoTypes";
import { renderWithRedux } from "../utils/testUtils";
import TodoPage from "../components/TodoPage";
import { fireEvent, screen, waitFor } from "@testing-library/react";

describe('Testing components', () => {
    const mockState: ToDoState = {
        items: [
            { id: '1', text: 'Test 1', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW" },
            { id: '2', text: 'Test 2', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW" },
            { id: '3', text: 'Test 3', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW" }
        ],
        totalCount: 3,
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
    };

    it('should display todos from store', async () => {
        renderWithRedux(<TodoPage />, { todos: mockState }); 
        
        // Add assertions to verify that the todos are rendered correctly
        expect(screen.getByText('Test 1')).toBeDefined();
        expect(screen.getByText('Test 2')).toBeDefined();
        expect(screen.getByText('Test 3')).toBeDefined();
    });

    it('should open new todo modal', async () => {
        renderWithRedux(<TodoPage />, { todos: mockState }); 
        
        const addButton = screen.getByText("+ New To Do");
        fireEvent.click(addButton);

        await waitFor(() => {
            expect(screen.getByText('Add Todo')).toBeDefined();
        })
    });

    it('should not display any stats', async () => {
        renderWithRedux(<TodoPage />, { todos: mockState }); 
        
        expect(screen.getByText('No information to display')).toBeDefined();
    });

    it('should display stats', async () => {
        const mockCopy = mockState;
        mockCopy.stats = {
            completed: 5,
            completedAvgTime: "05:00",
            completedHigh: 5,
            completedHighAvgTime: "05:00",
            completedLow: 0,
            completedLowAvgTime: "",
            completedMedium: 0,
            completedMediumAvgTime: ""
        }
        renderWithRedux(<TodoPage />, { todos: mockCopy }); 

        expect(screen.getByText('Average time to finish tasks')).toBeDefined();
    });



});
