import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../redux/store";
import { ToDo } from "../types/todoTypes";
import { ChangeEvent, useEffect, useState } from "react";
import { fetchToDos, removeTodo, toggleTodo } from "../redux/slices/todoSlice";
import TodoModal from "./TodoModal";

export default function TodoTable() {
    const dispatch = useDispatch<AppDispatch>();
    const { items } = useSelector((state: RootState) => state.todos);
    const [isModalOpen, setModalOpen] = useState(false);
    const [currentTodo, setCurrentTodo] = useState<ToDo | null>(null);

    useEffect(() => {
        dispatch(fetchToDos())
    },[dispatch]);

    const openEditModal = (todo: ToDo) => {
        setCurrentTodo(todo);
        setModalOpen(true);
    }

    const closeModal = () => {
        setModalOpen(false);
        setCurrentTodo(null);
    }

    const handleToggle = (_: ChangeEvent<HTMLInputElement>, todo: ToDo) => {
        dispatch(toggleTodo(todo));
    }

    const handleDelete = (id: string) => {
        dispatch(removeTodo(id));
    }

    return (
        <div className="flex items-center justify-center space-x-2 sm:rounded-sm shadow-sm">
            <table className="w-full text-left">
                <thead className="text-gray-700 uppercase bg-gray-50 py-7">
                    <tr>
                        <th className="px-6 py-3">Done</th>
                        <th className="px-6 py-3">Name</th>
                        <th className="px-6 py-3">
                            <div className="flex items-center">
                                Priority
                                {/** Insert sort logo */}
                                <span className="cursor-pointer">+</span>
                            </div>
                        </th>
                        <th className="px-6 py-3">
                            <div className="flex items-center">
                                Due Date
                                {/** Insert sort logo */}
                                <span className="cursor-pointer">+</span>
                            </div>
                        </th>
                        <th className="px-6 py-3">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    { items.length > 0 ? (
                        items.map((item : ToDo) => (
                            <tr key={item.id} className="bg-white border-b">
                                <td className="px-7 py-3">
                                    <input type="checkbox" checked={item.done} id={item.id} onChange={(e) => handleToggle(e, item)} />
                                </td>
                                <td className="px-6 py-3">{item.text}</td>
                                <td className="px-6 py-3">{item.priority.toLocaleUpperCase()}</td>
                                <td className="px-6 py-3">{item.dueDate ? new Date(item.dueDate).toLocaleDateString()  : '-'}</td>
                                <td className="px-6 py-3 text-right">
                                    <div className="flex justify-center">
                                        <button onClick={() => {openEditModal(item)}} className="font-medium px-2 py-2 bg-blue-600 text-white hover:bg-blue-500 rounded-md">Edit</button>
                                        <button onClick={() => {handleDelete(item.id)}} className="font-medium px-2 py-2 bg-red-600 text-white hover:bg-red-500 rounded-md">Delete</button>
                                    </div>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan={5} className="text-center">
                                No elements to display
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
            <TodoModal isOpen={isModalOpen} onClose={closeModal} todo={currentTodo} isEditing={true} />
        </div>
    )
}
