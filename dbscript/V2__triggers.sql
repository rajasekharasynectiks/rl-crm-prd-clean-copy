
--CREATE TRIGGER trg_update_version_quotations
--ON quotations
--AFTER UPDATE
--AS
--BEGIN
--    update quotations set version = (select max(COALESCE (q.version,0))+1 from quotations q inner join inserted i on q.id = i.id)
--    where id = (select q1.id from quotations q1 inner join inserted i1 on q1.id = i1.id)
--END;
