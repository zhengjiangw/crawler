
create table jd_item (
    id bigint primary key auto_increment comment '主键',
    spu bigint not null comment 'spu id',
    sku bigint not null comment 'sku id',
    title varchar(256) not null comment '标题',
    price decimal(10, 2) not null comment '价格',
    pic varchar(256) comment '图片路径',
    url varchar(256) not null comment '商品详情地址',
    created datetime not null DEFAULT CURRENT_TIMESTAMP comment '创建时间',
    updated datetime not null ON UPDATE CURRENT_TIMESTAMP comment '更新时间'
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
