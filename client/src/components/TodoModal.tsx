import { useDispatch } from "react-redux";
import { ToDo } from "../types/todoTypes";
import { AppDispatch } from "../redux/store";
import { ChangeEvent, useEffect, useState } from "react";
import { createToDo, updateToDo } from "../redux/slices/todoSlice";
import { formatForDisplay, formatForInput, parseCreationDate, parseInputDate } from "../utils/dateUtils";

type TodoModalProps = {
    isOpen: boolean;
    onClose: () => void;
    todo: ToDo | null;
    isEditing: boolean;
}

type ToDoForm = {
    text: string;
    priority: ToDo['priority'];
    dueDate: string;
}


export default function TodoModal({isOpen, onClose, todo} : TodoModalProps) {
    const dispatch = useDispatch<AppDispatch>();
    const [formData, setFormData] = useState<ToDoForm>({
        text: todo?.text || '',
        priority: todo?.priority || "LOW",
        dueDate: todo?.dueDate ? formatForInput(todo.dueDate) : ""
    });

    useEffect(() => {
        if (todo) {
            setFormData({
                text: todo.text,
                priority: todo.priority,
                dueDate: todo?.dueDate ? formatForInput(todo.dueDate) : ""
            });
        } else {
            setFormData({
                text: "",
                priority: "LOW",
                dueDate: ""
            })
        }
    }, [todo]);

    const validate = () : string | null => {
        if(formData.text.length < 3 || formData.text.length > 120) {
            return "Name should be from 3 to up to 120 characters."
        }

        if (formData.dueDate !== "") {
            let creationDate = todo ? todo.creationDate : new Date();
            // Remove the time
            creationDate = parseCreationDate(creationDate);
            const dueDate = parseInputDate(formData.dueDate);

            if( dueDate < creationDate ) {
                return "Due Date can't be set before the creation date: " + formatForDisplay(creationDate);
            }
        }
        
        return null;
    }

    const hasChanges = () => {
        if (todo) {
            const dueDate = todo.dueDate ? formatForInput(todo.dueDate) : "";
            return (
                formData.text !== todo.text ||
                formData.priority !== todo?.priority ||
                formData.dueDate !== dueDate
            )
        }
        return true;
        
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name] : value
        }));
    }
    
    const handleSubmit = () => {
        const error = validate();
        if (error) {
            alert(error);
            return;
        }
        if (todo && todo.id) {
            dispatch(updateToDo({id: todo.id, todoForm: formData})); 
        } else {
            dispatch(createToDo(formData))
        }
        onClose();
    }
    // TODO: ADD disabled fields to show fields like creationDate, doneDate, etc.
    return isOpen ? (
        <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white p-6 rounded shadow-lg w-96">
                <h2 className="text-xl mb-4">{todo ? 'Edit Todo' : 'Add Todo'}</h2>
                <form className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium">Text</label>
                        <input
                        name="text"
                        type="text"
                        value={formData.text}
                        onChange={handleInputChange}
                        className="w-full border rounded p-2"
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium">Priority</label>
                        <select
                        name="priority"
                        value={formData.priority}
                        onChange={handleInputChange}
                        className="w-full border rounded p-2"
                        >
                        <option value="HIGH">High</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LOW">Low</option>
                        </select>
                    </div>
                    <div>
                        <label className="block text-sm font-medium">Due Date</label>
                        <input
                        name="dueDate"
                        type="date"
                        value={formData.dueDate}
                        onChange={(e) => {
                            setFormData({...formData, dueDate: e.target.value ? e.target.value : ""})
                        }}
                        className="w-full border rounded p-2"
                        />
                    </div>
                </form>
                <div className="mt-4 flex justify-end space-x-2">
                    <button onClick={onClose} className="px-4 py-2 bg-gray-200 rounded">Cancel</button>
                    <button onClick={handleSubmit} className="px-4 py-2 bg-blue-500 text-white rounded disabled:bg-blue-100" disabled={!hasChanges() || formData.text.length < 3 || formData.text.length > 120}>Save</button>
                </div>
            </div>
        </div>
    ) : null
}
