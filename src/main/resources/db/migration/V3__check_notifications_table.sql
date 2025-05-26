-- Проверяем наличие колонки task_id
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'notifications'
        AND column_name = 'task_id'
    ) THEN
        ALTER TABLE notifications ADD COLUMN task_id VARCHAR(255) NOT NULL;
    END IF;
END $$; 