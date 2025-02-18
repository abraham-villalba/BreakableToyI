import axios, { AxiosError } from "axios";
import { ToDoFormForApi } from "../types/todoTypes";

const API_URL = 'http://localhost:9090/todos';

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

export const getTodos = async (queryParameters: string) => {
    try {
        const response = await axios.get(`${API_URL}${queryParameters}`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

export const getStats = async () => {
    try {
        const response = await axios.get(`${API_URL}/stats`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

export const updateTodo = async (id: string, data: ToDoFormForApi) => {
    try {
        const response = await axios.put(`${API_URL}/${id}`, data);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

export const deleteTodo = async (id: string) => {
    try {
        const response = await axios.delete(`${API_URL}/${id}`);
        return response.status;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

export const completeTodo = async (id: string) => {
    try {
        const response = await axios.put(`${API_URL}/${id}/done`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

export const uncompleteTodo = async (id: string) => {
    try {
        const response = await axios.put(`${API_URL}/${id}/undone`);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

export const createTodo = async (data: ToDoFormForApi) => {
    try {
        const response = await axios.post(API_URL, data);
        return response.data;
    } catch (error) {
        handleApiError(error as AxiosError);
    }
}

