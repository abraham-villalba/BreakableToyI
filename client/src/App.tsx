import { useDispatch, useSelector } from "react-redux"
import { AppDispatch, RootState } from "./redux/store"
import { fetchToDos } from "./redux/slices/todoSlice";

function App() {
  const dispatch = useDispatch<AppDispatch>();
  const { items, totalCount, status } = useSelector((state: RootState) => state.todos);

  return (
    <>
      <h1 className='text-6xl'>ToDo App</h1>
      <ul>
        <li>{items.length}</li>
        <li>{totalCount}</li>
        <li>{status}</li>
      </ul>
      <button onClick={() => {dispatch(fetchToDos())}}>Get Todos</button>
    </>
  )
}

export default App
