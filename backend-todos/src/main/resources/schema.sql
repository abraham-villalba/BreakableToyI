
create table to_do (
    id uuid not null, 
    creation_date timestamp(6), 
    done boolean, 
    done_date timestamp(6), 
    due_date timestamp(6), 
    priority tinyint not null check (priority between 0 and 2), 
    text varchar(120) not null, 
    primary key (id)
);