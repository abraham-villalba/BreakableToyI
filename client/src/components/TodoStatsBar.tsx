import { useSelector } from "react-redux";
import { RootState } from "../redux/store";

/**
 * TodoStatsBar component.
 * 
 * This component displays statistics about the tasks.
 * 
 * @component
 * @example
 * return (
 * <TodoStatsBar />
 * )
 * 
 */
export default function TodoStatsBar() {
	const { stats } = useSelector((state: RootState) => state.todos);
  
	return  (
		<section className="mt-2 w-full py-4 bg-sky-900 text-gray-50">
			{stats && stats.completed > 0 ? (
				<div className="max-w-4xl mx-auto flex sm:justify-around sm:flex-row flex-col align-middle justify-around">		
					<div className="w-full flex flex-col justify-around my-2">
						<p className="text-center w-full font-bold">Average time to finish tasks</p>
						<p className="text-center w-full text-sm">{stats.completedAvgTime} minutes</p>
					</div>
					<div className="w-full text-center sm:text-left justify-around">
						<ul className="flex flex-col">
							<li className="font-bold">Average time to finish tasks by priority:</li>
							<li className="text-sm"><span className="font-bold">Low: </span>{stats.completedLowAvgTime !== "" ? `${stats.completedLowAvgTime} minutes` : "No information available"}</li>
							<li className="text-sm"><span className="font-bold">Medium: </span>{stats.completedMediumAvgTime !== "" ? `${stats.completedMediumAvgTime} minutes` : "No information available"}</li>
							<li className="text-sm"><span className="font-bold">High: </span>{stats.completedHighAvgTime !== "" ? `${stats.completedHighAvgTime} minutes` : "No information available"}</li>
						</ul>
						
					</div>
				</div>
			) : (
				<div className="text-center mb-11">
					No information to display
				</div>
			)}
			
		</section>
  	)
}
