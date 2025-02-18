/**
 * @file This file contains all the types related to the ToDo feature.
 */

/**
 * ToDo
 * 
 * Represents a ToDo item.
 * 
 * @typedef {Object} ToDo
 * @property {string} id - The unique identifier of the ToDo.
 * @property {Date} creationDate - The date the ToDo was created.
 * @property {Date | null} dueDate - The date the ToDo is due.
 * @property {Date | null} doneDate - The date the ToDo was completed.
 * @property {string} text - The text or description of the ToDo.
 * @property {boolean} done - Whether the ToDo is completed.
 * @property {'LOW' | 'MEDIUM' | 'HIGH'} priority - The priority of the ToDo.
 */
export type ToDo = {
    id: string;
    creationDate: Date;
    dueDate: Date | null;
    doneDate: Date | null;
    text: string;
    done: boolean;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
}

/**
 * ToDoForm
 * 
 * Represents the form data for creating or updating a ToDo item.
 * 
 * @typedef {Object} ToDoForm
 * @property {Date | null} dueDate - The date the ToDo is due.
 * @property {string} text - The text or description of the ToDo.
 * @property {'LOW' | 'MEDIUM' | 'HIGH'} priority - The priority of the ToDo.
 */
export type ToDoForm = {
    dueDate: Date | null;
    text: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
}

/**
 * ToDoFormForApi
 * 
 * Represents the form data for creating or updating a ToDo item for the API.
 * 
 * @typedef {Object} ToDoFormForApi
 * @property {string | null} dueDate - The date the ToDo is due.
 * @property {string} text - The text or description of the ToDo.
 * @property {'LOW' | 'MEDIUM' | 'HIGH'} priority - The priority of the ToDo.
 */
export type ToDoFormForApi = Omit<ToDoForm, 'dueDate'> & {
    dueDate: string | null;
}

/**
 * ToDoFilter
 * 
 * Represents the filter options for the ToDo list.
 * 
 * @typedef {Object} ToDoFilter
 * @property {string | null} text - The text to filter by.
 * @property {'LOW' | 'MEDIUM' | 'HIGH' | null} priority - The priority to filter by.
 * @property {boolean | null} done - The completion status to filter by.
 */
export type ToDoFilter = {
    text: string | null;
    priority: ToDo['priority'] | null;
    done: boolean | null;
}

/**
 * Pagination
 * 
 * Represents the pagination information.
 * 
 * @typedef {Object} Pagination
 * @property {number} currentPage - The current page.
 * @property {number} pageSize - The number of items per page.
 * @property {number} totalPages - The total number of pages.
 * @property {boolean} isLast - Whether the current page is the last page.
 */
type Pagination = {
    currentPage: number;
    pageSize: number;
    totalPages: number;
    isLast: boolean;
}

/**
 * Sort
 * 
 * Represents the sorting information.
 * 
 * @typedef {Object} Sort
 * @property {string} field - The field to sort by.
 * @property {boolean} asc - Whether to sort in ascending order.
 */
export type Sort = {
    field: string;
    asc: boolean;
}

/**
 * ToDoState
 * 
 * Represents the state of the ToDo feature.
 * 
 * @typedef {Object} ToDoState
 * @property {ToDo[]} items - The list of ToDo items.
 * @property {number} totalCount - The total number of ToDo items.
 * @property {Object | null} stats - The statistics of the ToDo items.
 * @property {'idle' | 'loading' | 'succeded' | 'failed'} status - The status of the request.
 * @property {string | null} error - The error message.
 * @property {Pagination} pagination - The pagination information.
 * @property {Sort[]} sortBy - The sorting information.
 * @property {ToDoFilter | null} filterBy - The filter options.
 */
export type ToDoState = {
    items: ToDo[];
    totalCount: number;
    stats: {
        completed: number;
        completedLow: number;
        completedHigh: number;
        completedMedium: number;
        completedAvgTime: string;
        completedLowAvgTime: string;
        completedMediumAvgTime: string;
        completedHighAvgTime: string;
    } | null;
    status: 'idle' | 'loading' | 'succeded' | 'failed';
    error: string | null;
    pagination: Pagination;
    sortBy: Sort[];
    filterBy: ToDoFilter | null;
}