import { useDispatch, useSelector } from "react-redux";
import { ToDoFilter } from "../types/todoTypes";
import { AppDispatch, RootState } from "../redux/store";
import { ChangeEvent, useState } from "react";
import { addFilterBy, fetchToDos } from "../redux/slices/todoSlice";

type ToDoFilterForm = {
    text: string;
    priority: ToDoFilter['priority'] | 'ALL';
    done: 'DONE' | 'UNDONE' | 'ALL';
}

export default function TodoFilterForm() {
    const dispatch = useDispatch<AppDispatch>();
    const { filterBy } = useSelector((state: RootState) => state.todos);
    const [formData, setFormData] = useState<ToDoFilterForm>({
        text: '',
        priority: 'ALL',
        done: 'ALL'
    });

    const hasChanges = () => {
        const normalizedFilterBy : ToDoFilterForm = {
            text: filterBy?.text || '',
            priority: filterBy?.priority || 'ALL',
            done: filterBy?.done === true ? 'DONE' : filterBy?.done === false ? 'UNDONE' : 'ALL'
        }

        return (
            formData.text !== normalizedFilterBy.text ||
            formData.priority !== normalizedFilterBy.priority ||
            formData.done !== normalizedFilterBy.done
        )
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name] : value
        }));
    }
    
    const handleSubmit = () => {
        // Parse thorugh the data and assing corresponding fields.
        const filters : ToDoFilter = {
            text: formData.text === "" ? null : formData.text,
            priority: formData.priority === "ALL" ? null : formData.priority,
            done: formData.done === "ALL" ? null : formData.done === "DONE" ? true : false
        };
        dispatch(addFilterBy(filters));
        dispatch(fetchToDos());
    }
    // TODO: ADD style
    return (
        <section className="fixed top-0 left-0 w-full bg-gray-200 py-4 shadow-md">
            <div className="max-w-3xl mx-auto px-4">
                <form onSubmit={(e) => e.preventDefault()}>
                    <div className="flex">
                        <label className="block text-sm font-medium self-center pe-5">Name</label>
                        <input
                        name="text"
                        type="text"
                        value={formData.text ? formData.text : ""}
                        onChange={handleInputChange}
                        className="w-full border rounded p-1"
                        />
                    </div>
                    <div className="flex justify-between">
                        <div className="w-1/2 pt-3">
                            <div className="flex">
                                <label className="block text-sm font-medium pe-3 self-center">Priority</label>
                                <select
                                    name="priority"
                                    value={formData.priority ? formData.priority : "ALL"}
                                    onChange={handleInputChange}
                                    className="w-full border rounded p-1"
                                    >
                                    <option value="ALL">All</option>
                                    <option value="HIGH">High</option>
                                    <option value="MEDIUM">Medium</option>
                                    <option value="LOW">Low</option>
                                </select>
                            </div>
                            <div className="pt-3 flex">
                                <label className="block text-sm font-medium self-center pe-6">State</label>
                                <select
                                    name="done"
                                    value={formData.done}
                                    onChange={handleInputChange}
                                    className="w-full border rounded p-1"
                                    >
                                    <option value="ALL">All</option>
                                    <option value="DONE">Done</option>
                                    <option value="UNDONE">Undone</option>
                                </select>
                            </div>
                        </div>
                        
                        <div className="mt-4 flex self-end space-x-2">
                            <button onClick={handleSubmit} className="px-4 py-2 bg-blue-600 hover:bg-blue-500 text-white rounded disabled:bg-blue-300" disabled={!hasChanges()}>Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </section>
    )
}
