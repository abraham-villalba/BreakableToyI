import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../redux/store";
import { ToDo } from "../types/todoTypes";
import { ChangeEvent, useEffect } from "react";
import { addSortBy, deleteToDoAndUpdateStats, fetchToDos, fetchToDosAndStats, toggleToDoAndUpdateStats } from "../redux/slices/todoSlice";
import { formatForDisplay } from "../utils/dateUtils";

type TodoTableProps = {
    handleEdit: (todo: ToDo) => void;
}


export default function TodoTable({handleEdit} : TodoTableProps) {
    const dispatch = useDispatch<AppDispatch>();
    const { items, status } = useSelector((state: RootState) => state.todos);

    useEffect(() => {
        if (status === 'idle') {
            // Get initial list of todos and current statistics
            dispatch(fetchToDosAndStats());
        }
    },[dispatch]);

    const handleToggle = (_: ChangeEvent<HTMLInputElement>, todo: ToDo) => {
        // // Get the updated stats after completing or uncompleting a ToDo
        dispatch(toggleToDoAndUpdateStats(todo));
    }

    const handleDelete = (id: string) => {
        // // A completed task might've been deleted
        dispatch(deleteToDoAndUpdateStats(id));
        
    }

    const handleSortField = (field: string) => {
        dispatch(addSortBy(field));
        dispatch(fetchToDos());
    }

    return (
        <main className="mt-2 max-w-5xl mx-auto px-4">
            <div className="h-[calc(90vh-144px-96px)] overflow-auto shadow-md rounded border border-gray-300">
                <table className="w-full text-left">
                    <thead className="text-gray-50 bg-sky-800 py-7 sticky top-0">
                        <tr>
                            <th className="px-6 py-3">Done</th>
                            <th className="px-6 py-3">Name</th>
                            <th className="px-6 py-3">
                                <div className="flex items-center">
                                    <span className="cursor-pointer hover:text-white hover:underline" onClick={() => handleSortField("priority")}>Priority{'<>'}</span>
                                </div>
                            </th>
                            <th className="px-6 py-3 mx-auto">
                                <span className="cursor-pointer hover:text-white hover:underline" onClick={() => handleSortField("dueDate")}>Due Date{'<>'}</span>
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
                                    <td className={`${item.done ? "line-through" : ""} px-6 py-3 text-sm`}>{item.text}</td>
                                    <td className="px-6 py-3 text-sm">{item.priority.toLocaleUpperCase()}</td>
                                    <td className="px-6 py-3 text-sm">{item.dueDate ? formatForDisplay(new Date(item.dueDate))  : '-'}</td>
                                    <td className="px-6 py-3 text-right">
                                        <div className="flex justify-between">
                                            <button onClick={() => {handleEdit(item)}} className="font-normal text-sm px-2 py-2 bg-sky-600 text-white hover:bg-sky-500 rounded-md me-1">Edit</button>
                                            <button onClick={() => {handleDelete(item.id)}} className="font-normal text-sm px-2 py-2 bg-red-600 text-white hover:bg-red-500 rounded-md">Delete</button>
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
            </div>
        </main>
    )
}
