-- Create users table
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Create dogs table
CREATE TABLE dogs (
                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                      owner_id UUID NOT NULL REFERENCES users(id),
                      name VARCHAR(100) NOT NULL,
                      breed VARCHAR(100) NOT NULL,
                      gender VARCHAR(20) NOT NULL,
                      weight DECIMAL(5,2),
                      age VARCHAR(20),
                      is_neutered BOOLEAN DEFAULT false,
                      behavior TEXT,
                      pedigree BOOLEAN DEFAULT false,
                      created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);