import { describe, it, expect } from "vitest";
import todoReducer, {addFilterBy, setCurrentPage, insertItem, removeItemWithId, updateItem, addSortBy, setStats} from "../redux/slices/todoSlice";
import { ToDo, ToDoFilter, ToDoState } from "../types/todoTypes";

describe('todoSlice reducers', () => {
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

    it('should add a filter', () => {
        const filter: ToDoFilter = {
            priority: "HIGH",
            text: "Lorem ipmsum",
            done: null
        }
        const nextState = todoReducer(initialState, addFilterBy(filter));
        expect(nextState.filterBy).toEqual(filter);
    });

    it('should set the current page', () => {
        const nextState = todoReducer(initialState, setCurrentPage(2));
        expect(nextState.pagination.currentPage).toBe(2);
    });

    it('should insert a todo item', () => {
        const newTodo: ToDo = { id: '1', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"}
        const nextState = todoReducer(initialState, insertItem(newTodo));
        expect(nextState.items[0]).toEqual(newTodo);
        expect(nextState.items.length).toEqual(1);
        expect(nextState.totalCount).toEqual(1);
    });

    it('should remove an item', () => {
        const auxState = initialState;
        auxState.items = [
            { id: '1', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"},
            { id: '2', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"},
            { id: '3', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"}
        ]
        auxState.totalCount = auxState.items.length;
        const nextState = todoReducer(auxState, removeItemWithId('2'));
        expect(nextState.items.length).toEqual(2);
        expect(nextState.totalCount).toEqual(2);
        expect(nextState.items[1]).toEqual(auxState.items[2]); 
    });

    it('should update an item', () => {
        const auxState = initialState;
        auxState.items = [
            { id: '1', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"},
            { id: '2', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"},
            { id: '3', text: 'Test', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "LOW"}
        ]
        auxState.totalCount = auxState.items.length;
        const updatedItem: ToDo = { id: '1', text: 'Test updated', done: false, doneDate: null, creationDate: new Date(), dueDate: null, priority: "HIGH"};
        const nextState = todoReducer(auxState, updateItem(updatedItem));
        expect(nextState.items.length).toEqual(3);
        expect(nextState.totalCount).toEqual(3);
        expect(nextState.items[0]).toEqual(updatedItem); 
    });

    it('should add a sort option', () => {
        const nextState = todoReducer(initialState, addSortBy('priority'));
        expect(nextState.sortBy.length).toEqual(1);
        expect(nextState.sortBy[0]).toEqual({field: 'priority', asc: true});
    });

    it('should toggle the sort order if field in sortBy', () => {
        const auxState = initialState;
        auxState.sortBy = [{field: 'priority', asc: true}];
        const nextState = todoReducer(auxState, addSortBy('priority'));
        expect(nextState.sortBy.length).toEqual(1);
        expect(nextState.sortBy[0]).toEqual({field: 'priority', asc: false});
    });

    it('should set the statistics', () => {
        const stats = {
            totalDone: 10,
            averageDoneTime: "2m 10s",
            totalHighDone: 7,
            averageHighDoneTime: "2m 30s",
            totalLowDone: 3,
            averageLowDoneTime: "1m 30s",
            totalMediumDone: 0,
            averageMediumDoneTime: ""
        }
        const nextState = todoReducer(initialState, setStats(stats));
        expect(nextState.stats?.completed).toEqual(stats.totalDone);
    });
    
    
})