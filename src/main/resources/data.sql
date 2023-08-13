insert into users_tbl(name, email) values
('Arjun','arjun@xyz.com'),
('Yudhishtir','yudhishtir@xyz.com');

select SET(@arjun, id) from users_tbl where email = 'arjun@xyz.com';
select SET(@yudhishtir, id) from users_tbl where email = 'arjun@xyz.com';

insert into schedule_tbl(user_id, name, timezone, description) values
(@arjun, 'Train Archery', 'Asia/Kolkata', 'Archery practise session'),
(@arjun, 'Duel with Arjun', 'Asia/Kolkata', 'Friendly duel session');

select set(@trainArcheryWithArjun, id) from schedule_tbl where user_id = @arjun and name = 'Train Archery';
select set(@duelWithArjun, id) from schedule_tbl where user_id = @arjun and name = 'Duel with Arjun';

insert into availability_tbl(schedule_id, weekday, start_time_in_sec, duration_in_sec,is_available) values
(@trainArcheryWithArjun, 'MONDAY', 3600*17, 3600*3, true),
(@trainArcheryWithArjun, 'SATURDAY', 3600*10, 3600*3, true),
(@trainArcheryWithArjun, 'SATURDAY', 3600*15, 3600*3, true),

(@duelWithArjun, 'SUNDAY', 3600*17, 3600*3, true),
(@duelWithArjun, 'FRIDAY', 3600*10, 3600*3, true),
(@duelWithArjun, 'FRIDAY', 3600*15, 3600*3, true);
