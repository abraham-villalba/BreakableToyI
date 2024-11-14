
create table to_do (
    id uuid not null, 
    creation_date date, 
    done boolean, 
    done_date date, 
    due_date date, 
    priority tinyint not null check (priority between 0 and 2), 
    text varchar(120) not null, 
    primary key (id)
);