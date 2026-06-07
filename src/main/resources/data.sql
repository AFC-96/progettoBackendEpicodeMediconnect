-- Inserisce il ruolo 'PATIENT' nella tabella roles.
-- Se il ruolo esiste già (conflitto sulla colonna 'name'), l'operazione viene ignorata senza generare errori.
-- Questo assicura che il ruolo sia presente senza duplicati ad ogni avvio dell'applicazione.
INSERT INTO roles (name) VALUES ('PATIENT') ON CONFLICT (name) DO NOTHING;

-- Inserisce il ruolo 'DOCTOR' nella tabella roles.
-- Gestisce il conflitto come sopra: se il ruolo esiste, non fa nulla.
INSERT INTO roles (name) VALUES ('DOCTOR') ON CONFLICT (name) DO NOTHING;

-- Inserisce il ruolo 'ADMIN' nella tabella roles.
-- Anche qui, evita duplicati se il ruolo è già stato creato in una esecuzione precedente.
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT (name) DO NOTHING;