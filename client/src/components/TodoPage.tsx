import PaginationBar from "./PaginationBar";
import TodoTable from "./TodoTable";

//import { useDispatch, useSelector } from "react-redux";
//import { AppDispatch, RootState } from "../redux/store";
import { ToDo } from "../types/todoTypes";
import { useState } from "react";
// import { fetchToDos, removeTodo, toggleTodo } from "../redux/slices/todoSlice";
import TodoModal from "./TodoModal";


export default function TodoPage() {
    //const dispatch = useDispatch<AppDispatch>();
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
            <div className="flex justify-between items-center mb-4">
                <button onClick={() => {setModalOpen(true)}} className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-300">Add Todo</button>
            </div>
            <TodoTable handleEdit={handleEdit} />
            <TodoModal isOpen={isModalOpen} onClose={closeModal} todo={currentTodo} isEditing={currentTodo !== null} />
            <PaginationBar />
        </>
    )
}
