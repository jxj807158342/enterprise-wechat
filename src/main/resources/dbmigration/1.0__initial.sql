-- apply changes
create table demo (
  id                            bigint auto_increment not null,
  name                          varchar(255) comment '姓名',
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_demo primary key (id)
) comment='测试';

