import TodoTable from "./components/TodoTable";
import PaginationBar from "./components/PaginationBar";

function App() {

  return (
    <>
      <h1 className='text-6xl'>ToDo App</h1>
      <TodoTable />
      <PaginationBar />
    </>
  )
}

export default App
