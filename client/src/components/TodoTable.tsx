import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../redux/store";
import { ToDo } from "../types/todoTypes";
import { useEffect } from "react";
import { fetchToDos } from "../redux/slices/todoSlice";

export default function TodoTable() {
    const dispatch = useDispatch<AppDispatch>();
    const { items } = useSelector((state: RootState) => state.todos);
    const currentPage = useSelector((state: RootState) => state.todos.pagination.currentPage)

    useEffect(() => {
        dispatch(fetchToDos())
    },[dispatch, currentPage])

    return (
        <div>
            <table>
                <thead>
                    <tr>
                        <th>Done</th>
                        <th>Name</th>
                        <th>Priority</th>
                        <th>Due Date</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    { items.length > 0 ? (
                        items.map((item : ToDo) => (
                            <tr key={item.id}>
                                <td>
                                    <input type="checkbox" checked={item.done} id={item.id} onChange={() => {console.log('I clicked the checkbox for ' + item.text)}} />
                                </td>
                                <td>{item.text}</td>
                                <td>{item.priority.toLocaleUpperCase()}</td>
                                <td>{item.dueDate ? new Date(item.dueDate).toLocaleDateString()  : '-'}</td>
                                <td>
                                    <button>Edit</button>
                                    <button>Delete</button>
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
    )
}
