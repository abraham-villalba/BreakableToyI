export type ToDo = {
    id: string;
    creationDate: Date;
    dueDate: Date | null;
    doneDate: Date | null;
    text: string;
    done: boolean;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
}

export type ToDoForm = {
    dueDate: Date | null;
    text: string;
    priority: 'LOW' | 'MEDIUM' | 'HIGH';
}

export type ToDoFormForApi = Omit<ToDoForm, 'dueDate'> & {
    dueDate: string | null;
}

export type ToDoFilter = {
    text: string | null;
    priority: ToDo['priority'] | null;
    done: boolean | null;
}

type Pagination = {
    currentPage: number;
    pageSize: number;
    totalPages: number;
    isLast: boolean;
}

export type Sort = {
    field: string;
    asc: boolean;
}

export type ToDoState = {
    items: ToDo[];
    totalCount: number;
    stats: {
        completedAvgTime: string;
        completedLowAvgTime: string | null;
        completedMediumAvgTime: string | null;
        completedHighAvgTime: string | null;
    } | null;
    status: 'idle' | 'loading' | 'succeded' | 'failed';
    error: string | null;
    pagination: Pagination;
    sortBy: Sort[];
    filterBy: ToDoFilter | null;
}