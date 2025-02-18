import { useSelector } from "react-redux";
import { RootState } from "../redux/store";

export default function FeedbackModal() {
    const { status } = useSelector((state: RootState) => state.todos);

    if (status !== 'loading') {
        return null;
    }

    return (
        <div className="fixed inset-0 bg-gray-800 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white p-6 rounded shadow-lg w-96 flex flex-col items-center">
                <div className="loader ease-linear rounded-full border-8 border-t-8 border-gray-200 h-24 w-24 mb-4"></div>
                <p className="text-center">Loading, please wait...</p>
            </div>
        </div>
    );
}