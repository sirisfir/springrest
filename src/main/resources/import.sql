insert into `user`(`username`,`password`,`role`,`status`,`name`) values ('Peter',concat('{MD5}',md5('superpig')),'editor','inactive','Peter Griffin');
insert into `user`(`username`,`password`,`role`,`status`,`name`) values ('Lois',concat('{MD5}',md5('collegegirl')),'editor','active','Lois Lan... (sorry) Griffin');
insert into `user`(`username`,`password`,`role`,`status`,`name`) values ('Meg',concat('{MD5}',md5('lifeisdark')),'editor','active','Meg Griffin(dor)');
insert into `user`(`username`,`password`,`role`,`status`,`name`) values ('Chris',concat('{MD5}',md5('boobs')),'editor','active','Tha Chris');
insert into `user`(`username`,`password`,`role`,`status`,`name`) values ('Stewie',concat('{MD5}',md5('killlois')),'editor','active','Stewie TakeOverTheWorld');
insert into `user`(`username`,`password`,`role`,`status`,`name`) values ('Brian',concat('{MD5}',md5('goodboy')),'admin','active','Brian Doggo');

insert into `article`(`author`,`publish_date`,`article_type`,`title`,`text`)
select `id`,current_timestamp,'BLOG','Dear diary','Guess what I finally caught today' from `user` where `username`='Brian'
union select `id`,current_timestamp,'BLOG','The Art of War','Lois must die. It is the ultimate goal.' from `user` where `username`='Stewie'
union select `id`,current_timestamp,'FRONT_PAGE','Lipshit','Why cheap lipstick is bullshit.' from `user` where `username`='Meg'
union select `id`,current_timestamp,'FRONT_PAGE','Icecream pancakes','Borrow 3 eggs from Cleveland...' from `user` where `username`='Lois'; 
