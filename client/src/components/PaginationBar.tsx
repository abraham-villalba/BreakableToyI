import { useDispatch, useSelector } from "react-redux";
import { AppDispatch, RootState } from "../redux/store";
import { fetchToDos, setCurrentPage } from "../redux/slices/todoSlice";


export default function PaginationBar() {
    const dispatch = useDispatch<AppDispatch>();
    const {currentPage, totalPages, isLast} = useSelector((state: RootState) => state.todos.pagination)
    
    const isPreviousDisabled = () => currentPage === 0;

    const handlePageClick = (page : number) => {
        if (page !== currentPage) {
            dispatch(setCurrentPage(page))
            dispatch(fetchToDos())
        }
    }

    const handlePreviousClick = () => {
        if(!isPreviousDisabled()) {
            dispatch(setCurrentPage(currentPage - 1))
            dispatch(fetchToDos())
        }
    }

    const handleNextClick = () => {
        if(!isLast) {
            dispatch(setCurrentPage(currentPage + 1))
            dispatch(fetchToDos())
        }
    }

    const handleFirstClick = () => {
        dispatch(setCurrentPage(0))
        dispatch(fetchToDos())
    }

    const handleLastClick = () => {
        dispatch(setCurrentPage(totalPages - 1))
        dispatch(fetchToDos())
    }

    const getPageRange = (current: number, totalPages: number): number[] => {
        // Ensure the totalPages is at least the current page (edge case prevention)
        if (totalPages < current) return [];
      
        const start = Math.max(1, current - 1); // Start range: one before current, but not below 1
        const end = Math.min(totalPages, current + 1); // End range: one after current, but not above totalPages

        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    };

    const pages = getPageRange(currentPage + 1, totalPages)

    
      
    
    return (
        <div className="mt-4 max-w-4xl mx-auto">
            <div className="flex items-center justify-center space-x-2">
                {/* First Button */}
                
                <button
                    onClick={handleFirstClick}
                    disabled={totalPages === 0 || isPreviousDisabled()}
                    className={`px-4 py-2 text-sm ${
                    totalPages === 0 || isPreviousDisabled()
                        ? "text-gray-400 cursor-not-allowed"
                        : "text-blue-500 hover:text-blue-700"
                    }`}
                >
                    {'<<'}
                </button>
                <button
                    onClick={handlePreviousClick}
                    disabled={isPreviousDisabled()}
                    className={`px-4 py-2 text-sm ${
                    isPreviousDisabled()
                        ? "text-gray-400 cursor-not-allowed"
                        : "text-blue-500 hover:text-blue-700"
                    }`}
                >
                    {'<'}
                </button>
                {/* Page Numbers */}
                {pages.map((page) => (
                    <button
                    key={page}
                    onClick={() => handlePageClick(page - 1)}
                    disabled={page - 1 === currentPage}
                    className={`px-4 py-2 text-sm rounded ${
                        page - 1 === currentPage
                        ? "bg-blue-500 text-white cursor-not-allowed"
                        : "bg-gray-100 text-gray-700 hover:bg-gray-200"
                    }`}
                    >
                    {page}
                    </button>
                ))}

                {/* Next Button */}
                <button
                    onClick={handleNextClick}
                    disabled={isLast}
                    className={`px-4 py-2 text-sm ${
                        isLast
                        ? "text-gray-400 cursor-not-allowed"
                        : "text-blue-500 hover:text-blue-700"
                    }`}
                >
                    Next
                </button>
                {/* Last Button */}
                <button
                    onClick={handleLastClick}
                    disabled={totalPages === 0 || isLast}
                    className={`px-4 py-2 text-sm ${
                    totalPages === 0 || isLast
                        ? "text-gray-400 cursor-not-allowed"
                        : "text-blue-500 hover:text-blue-700"
                    }`}
                >
                    {'>>'}
                </button>
            </div>
        </div>
        
    )
}
