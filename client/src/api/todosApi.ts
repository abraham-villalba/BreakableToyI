/**
 * @file This file contains the functions to interact with the API.
 */
import axios, { AxiosError } from "axios";
import { ToDoFormForApi } from "../types/todoTypes";

const API_URL = 'http://localhost:9090/todos';

/**
 * Handles an API error.
 * 
 * @param {AxiosError} error - The error object.
 */
const handleApiError = (error: AxiosError) => {
    if (error.response) {
        // Server responded with a status other than 2xx
        console.log(error.response);
        console.error('API Error:', error.response.data);
        throw new Error(error.message|| 'An error occurred');
    } else if (error.request) {
        // No response was received
        console.error('API Error: No response received', error.request);
        throw new Error('No response received from server');
    } else {
        // Something happened in setting up the request
        console.error('API Error:', error.message);
        throw new Error(error.message);
    }
};

/**
 * Fetches all the todos.
 * 
 * @param {string} queryParameters - The query parameters to filter the todos.
 * @returns {Promise} A promise that resolves to the list of todos.
 */
export const getTodos = async (queryParameters: string) => {
    try {
        const response = await axios.get(`${API_URL}${queryParameters}`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

/**
 * Fetches the statistics for the todos.
 * 
 * @returns {Promise} A promise that resolves to the statistics.
 */
export const getStats = async () => {
    try {
        const response = await axios.get(`${API_URL}/stats`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

/**
 * Updates a todo.
 * 
 * @param {string} id - The id of the todo to update.
 * @param {ToDoFormForApi} data - The data to update the todo with.
 * @returns {Promise} A promise that resolves to the updated todo.
 */
export const updateTodo = async (id: string, data: ToDoFormForApi) => {
    try {
        const response = await axios.put(`${API_URL}/${id}`, data);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

/**
 * Deletes a todo.
 * 
 * @param {string} id - The id of the todo to delete.
 * @returns {Promise} A promise that resolves to the status of the request.
 */
export const deleteTodo = async (id: string) => {
    try {
        const response = await axios.delete(`${API_URL}/${id}`);
        return response.status;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

/**
 * Completes a todo.
 * 
 * @param {string} id - The id of the todo to complete or done.
 * @returns {Promise} A promise that resolves to the updated todo.
 */
export const completeTodo = async (id: string) => {
    try {
        const response = await axios.put(`${API_URL}/${id}/done`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

/**
 * Uncompletes a todo.
 * 
 * @param {string} id - The id of the todo to uncomplete or undone.
 * @returns {Promise} A promise that resolves to the updated todo.
 */
export const uncompleteTodo = async (id: string) => {
    try {
        const response = await axios.put(`${API_URL}/${id}/undone`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

/**
 * Creates a todo.
 * 
 * @param {ToDoFormForApi} data - The data to create the todo with.
 * @returns {Promise} A promise that resolves to the created todo.
 */
export const createTodo = async (data: ToDoFormForApi) => {
    try {
        const response = await axios.post(API_URL, data);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

