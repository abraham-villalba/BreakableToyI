import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../redux/store";
import { ToDo } from "../types/todoTypes";
import { ChangeEvent, useEffect } from "react";
import { addSortBy, deleteToDoAndUpdateStats, fetchToDos, fetchToDosAndStats, setCurrentPage, toggleToDoAndUpdateStats } from "../redux/slices/todoSlice";
import { formatForDisplay } from "../utils/dateUtils";

type TodoTableProps = {
    handleEdit: (todo: ToDo) => void;
}

/**
 * TodoTable component.
 * 
 * This component displays a table of ToDos.
 * 
 * @param param0 - The handleEdit function.
 * @component
 * @example
 * return (
 *  <TodoTable handleEdit={handleEdit} />
 * )
 * 
 */
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
        if (items.length > 0) {
            dispatch(addSortBy(field));
            dispatch(setCurrentPage(0));
            dispatch(fetchToDos());
        }
        
    }

    // Get the color for the row based on the due date
    const getColor = (todo: ToDo) : string => {
        let bg = "";
        if (todo.dueDate && !todo.done) {
            const dueDate = new Date(todo.dueDate);
            const today = new Date();
            const diffInDays = (dueDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24);
            if (diffInDays <= 7) {
                bg = "bg-red-200"
            } else if (diffInDays <= 14 && diffInDays > 7) {
                bg = "bg-yellow-200"
            } else if (diffInDays > 14) {
                bg = "bg-green-200"
            }
        }
        return bg !== "" ? bg : "bg-white"
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
                        { items && items.length > 0 ? (
                            items.map((item : ToDo) => (
                                <tr key={item.id} className={`${getColor(item)} border-b`}>
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
