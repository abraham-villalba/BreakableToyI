import axios from "axios";
import { ToDoFormForApi } from "../types/todoTypes";

const API_URL = 'http://localhost:8080/todos';

export const getTodos = (queryParamenters: string) => {
    return axios.get(API_URL + queryParamenters)
}

export const updateTodo = (id: string, data: ToDoFormForApi) => {
    return axios.put(API_URL + '/' + id, data);
}

export const deleteTodo = (id: string) => {
    return axios.delete(API_URL + '/' + id);
}

export const completeTodo = (id: string) => {
    return axios.put(API_URL + '/' + id + '/done');
}

export const uncompleteTodo = (id: string) => {
    return axios.put(API_URL + '/' + id + '/undone');
}

export const createTodo = (data: ToDoFormForApi) => {
    return axios.post(API_URL, data);
}

