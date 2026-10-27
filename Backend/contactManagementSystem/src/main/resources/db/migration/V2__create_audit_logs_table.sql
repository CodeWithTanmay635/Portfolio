CREATE TABLE audit_logs (
    id UUID PRIMARY KEY,
    contact_id UUID NOT NULL,
    old_status VARCHAR(20) NOT NULL,
    new_status VARCHAR(20) NOT NULL,
    changed_by VARCHAR(100),
    note VARCHAR(500),
    changed_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_audit_contact_id ON audit_logs (contact_id);
CREATE INDEX idx_audit_changed_at ON audit_logs (changed_at);
