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
        <div className="">
            <div className="">
                <form className="" onSubmit={(e) => e.preventDefault()}>
                    <div>
                        <label className="block text-sm font-medium">Name</label>
                        <input
                        name="text"
                        type="text"
                        value={formData.text ? formData.text : ""}
                        onChange={handleInputChange}
                        className="w-full border rounded p-2"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium">Priority</label>
                        <select
                        name="priority"
                        value={formData.priority ? formData.priority : "ALL"}
                        onChange={handleInputChange}
                        className="w-full border rounded p-2"
                        >
                        <option value="ALL">All</option>
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium">State</label>
                        <select
                        name="done"
                        value={formData.done}
                        onChange={handleInputChange}
                        className="w-full border rounded p-2"
                        >
                        <option value="ALL">All</option>
                        <option value="DONE">Done</option>
                        <option value="UNDONE">Undone</option>
                        </select>
                    </div>
                </form>
                <div className="mt-4 flex justify-end space-x-2">
                    <button onClick={handleSubmit} className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-blue-100" disabled={!hasChanges()}>Search</button>
                </div>
            </div>
        </div>
    )
}
