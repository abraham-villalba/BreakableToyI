import { useDispatch } from "react-redux";
import { ToDo, ToDoForm } from "../types/todoTypes";
import { AppDispatch } from "../redux/store";
import { ChangeEvent, useEffect, useState } from "react";
import { fetchToDos } from "../redux/slices/todoSlice";

type TodoModalProps = {
    isOpen: boolean;
    onClose: () => void;
    todo?: ToDo;
    isEditing: boolean;
}


export default function TodoModal({isOpen, onClose, todo, isEditing} : TodoModalProps) {
    const dispatch = useDispatch<AppDispatch>();
    const [formData, setFormData] = useState<ToDoForm>({
        text: todo?.text || '',
        priority: todo?.priority || "LOW",
        dueDate: todo?.dueDate || null
    });

    useEffect(() => {
        if (isEditing && todo) {
            setFormData({
                text: todo.text,
                priority: todo.priority,
                dueDate: todo.dueDate
            });
        }
    }, [todo]);

    const validate = () : string | null => {
        
        return null;
    }

    const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const {name, value} = e.target;
        setFormData({...formData, [name]: value});
    }
    
    const handleSubmit = async () => {
        const error = validate();
        if(error) {
            alert(error);
            return;
        }
        //const action = isEditing ? updateTodo : createTodo;
        //await dispatch(action(isEditing ? { ...todo, ...formData } : formData as Todo));
        onClose();
        dispatch(fetchToDos())
    }

    return isOpen ? (
        <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white p-6 rounded shadow-lg w-96">
                <h2 className="text-xl mb-4">{isEditing ? 'Edit Todo' : 'Add Todo'}</h2>
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
                        onChange={handleInputChange}
                        className="w-full border rounded p-2"
                        />
                    </div>
                </form>
                <div className="mt-4 flex justify-end space-x-2">
                    <button onClick={onClose} className="px-4 py-2 bg-gray-200 rounded">Cancel</button>
                    <button onClick={handleSubmit} className="px-4 py-2 bg-blue-500 text-white rounded">Save</button>
                </div>
            </div>
        </div>
    ) : null
}
