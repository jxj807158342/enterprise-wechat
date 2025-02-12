-- apply changes
create table access_token (
  id                            bigint auto_increment not null,
  access_token                  varchar(255) comment 'token 值',
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_access_token primary key (id)
) comment='获取token';

