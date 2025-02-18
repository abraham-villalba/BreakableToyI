import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../redux/store";
import { clearError } from "../redux/slices/todoSlice";

/**
 * TodoModal component.
 * 
 * This component displays an error modal when an error occurs.
 * 
 * @component
 * @example
 * return (
 *  <TodoModal />
 * )
 * 
 */
export default function ErrorModal() {
    const { error } = useSelector((state: RootState) => state.todos);
    const dispatch = useDispatch<AppDispatch>();

    // Close the modal
    const onClose = () => {
        dispatch(clearError());
    }
    

    return error ? (
        <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white p-6 rounded shadow-lg w-96">
                <h2 className="text-xl mb-4">Error</h2>
                <p className="text-center">{error}</p>
                <div className="mt-4 flex justify-end space-x-2">
                    <button type="button" onClick={onClose} className="px-4 py-2 bg-gray-200 rounded">Close</button>
                </div>
            </div>
        </div>
    ) : null
}
