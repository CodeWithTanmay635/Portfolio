CREATE TABLE contacts (
                          id UUID PRIMARY KEY,
                          reference_id VARCHAR(30) NOT NULL UNIQUE,
                          name VARCHAR(100) NOT NULL,
                          email VARCHAR(100) NOT NULL,
                          message VARCHAR(2000) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          priority VARCHAR(20) NOT NULL,
                          priority_score INTEGER,
                          ip_address VARCHAR(45),
                          user_agent VARCHAR(500),
                          version BIGINT NOT NULL,
                          created_at TIMESTAMP WITH TIME ZONE NOT NULL,
                          updated_at TIMESTAMP WITH TIME ZONE,
                          replied_at TIMESTAMP WITH TIME ZONE,
                          admin_note VARCHAR(1000)
);

CREATE INDEX idx_contacts_status ON contacts (status);
CREATE INDEX idx_contacts_priority ON contacts (priority);
CREATE INDEX idx_contacts_email ON contacts (email);
CREATE INDEX idx_contacts_created ON contacts (created_at);
