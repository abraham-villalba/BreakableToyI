import PaginationBar from "./PaginationBar";
import TodoTable from "./TodoTable";
import TodoFilterForm from "./TodoFilterForm";
import { ToDo } from "../types/todoTypes";
import { useState } from "react";
import TodoModal from "./TodoModal";
import TodoStatsBar from "./TodoStatsBar";
import ErrorModal from './ErrorModal';
import FeedbackModal from "./FeedbackModal";

/**
 * TodoPage component
 * 
 * This component displays the main page for the ToDo feature.
 * 
 * @component
 * @example
 * return (
 * <TodoPage />
 * )
 * 
 */
export default function TodoPage() {
    const [isModalOpen, setModalOpen] = useState(false);
    const [currentTodo, setCurrentTodo] = useState<ToDo | null>(null);

    const handleEdit = (todo: ToDo) => {
        setCurrentTodo(todo);
        setModalOpen(true);
    }

    const closeModal = () => {
        setModalOpen(false);
        setCurrentTodo(null);
    }

    return (
        <>
            <TodoFilterForm />
            <div className="mb-2 mt-40 max-w-5xl mx-auto px-4">
                <button onClick={() => {setModalOpen(true)}} className="px-4 py-2 bg-sky-600 text-white rounded hover:bg-sky-500">+ New To Do</button>
            </div>
            <TodoTable handleEdit={handleEdit} />
            <TodoModal isOpen={isModalOpen} onClose={closeModal} todo={currentTodo} isEditing={currentTodo !== null} />
            <PaginationBar />
            <TodoStatsBar />
            <ErrorModal />
            <FeedbackModal />
        </>
    )
}
