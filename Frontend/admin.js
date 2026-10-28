(() => {
    "use strict";

    // --- Configuration & State ---
    const API = "https://portfolio-backend-wgvw.onrender.com/api/v1";
    const $ = s => document.querySelector(s);

    const state = {
        page: 0,
        size: 10,
        status: "",
        priority: "",
        query: "",
        messages: [],
        total: 0,
        totalPages: 0,
        selected: null,
        csrf: ""
    };

    // --- Utilities ---
    const esc = v => String(v ?? "").replace(/[&<>"']/g, c => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        '"': "&quot;",
        "'": "&#39;"
    }[c]));

    const cls = v => String(v || "").toLowerCase().replace(/[^a-z0-9_-]/g, "");

    function toast(message, error = false) {
        const region = $("#toast-region");
        if (!region) return;

        const node = document.createElement("div");
        node.className = "toast" + (error ? " error" : "");
        node.innerHTML = `
      <i class="${error ? 'ri-error-warning-line' : 'ri-checkbox-circle-line'}"></i>
      <span></span>
    `;
        node.querySelector("span").textContent = message;

        region.append(node);
        setTimeout(() => node.remove(), 4200);
    }

    function cookie(name) {
        const found = document.cookie.split("; ").find(x => x.startsWith(name + "="));
        return found ? decodeURIComponent(found.slice(name.length + 1)) : "";
    }

    function date(v, time = true) {
        if (!v) return "—";
        const d = new Date(v);
        if (Number.isNaN(d.getTime())) return String(v);

        const options = time
            ? { day: "2-digit", month: "short", year: "numeric", hour: "2-digit", minute: "2-digit" }
            : { day: "2-digit", month: "short", year: "numeric" };

        return new Intl.DateTimeFormat(undefined, options).format(d);
    }

    // --- API & Auth ---
    async function csrf() {
        const r = await fetch(API + "/auth/csrf", {
            credentials: "include",
            headers: { Accept: "application/json" }
        });
        const p = await r.json().catch(() => ({}));

        if (!r.ok) throw Error(p.message || "Could not initialize secure session.");

        state.csrf = p.token || cookie("XSRF-TOKEN");
        if (!state.csrf) throw Error("CSRF token missing. Check backend CSRF configuration.");

        return state.csrf;
    }

    async function request(path, options = {}) {
        const method = (options.method || "GET").toUpperCase();
        const headers = new Headers(options.headers || {});

        headers.set("Accept", "application/json");
        if (options.body && !headers.has("Content-Type")) {
            headers.set("Content-Type", "application/json");
        }

        if (!["GET", "HEAD", "OPTIONS", "TRACE"].includes(method)) {
            if (!state.csrf) await csrf();
            headers.set("X-XSRF-TOKEN", state.csrf);
        }

        const r = await fetch(API + path, { ...options, method, headers, credentials: "include" });
        const p = await r.json().catch(() => ({}));

        if (r.status === 401) {
            if (document.body.classList.contains("dashboard-page")) location.replace("admin-login.html");
            throw Error(p.message || "Session expired. Sign in again.");
        }
        if (r.status === 403) throw Error("Request blocked. Refresh the page and retry; the security token may have expired.");
        if (!r.ok || p.success === false) throw Error(p.message || "Request failed (" + r.status + ").");

        return p;
    }

    // --- Initialization ---
    async function initLogin() {
        const form = $("#admin-login-form");
        if (!form) return;

        if ($("#year")) $("#year").textContent = new Date().getFullYear();

        $("#toggle-password").addEventListener("click", () => {
            const input = $("#password");
            const show = input.type === "password";
            input.type = show ? "text" : "password";
            $("#toggle-password").innerHTML = `<i class="${show ? 'ri-eye-off-line' : 'ri-eye-line'}"></i>`;
            $("#toggle-password").setAttribute("aria-label", show ? "Hide password" : "Show password");
        });

        try {
            await csrf();
            const me = await request("/auth/me");
            if (me.authenticated) {
                location.replace("admin-dashboard.html");
                return;
            }
        } catch (e) {
            console.debug("No active session:", e.message);
        }

        form.addEventListener("submit", async e => {
            e.preventDefault();
            const b = $("#login-submit");
            $("#login-error").textContent = "";
            b.disabled = true;
            b.querySelector("span").textContent = "Authenticating…";

            try {
                await csrf();
                const result = await request("/auth/login", {
                    method: "POST",
                    body: JSON.stringify({
                        username: $("#username").value.trim(),
                        password: $("#password").value
                    })
                });

                if (!result.authenticated) throw Error("Authentication failed.");
                location.replace("admin-dashboard.html");
            } catch (err) {
                $("#login-error").textContent = err.message || "Unable to sign in.";
                b.disabled = false;
                b.querySelector("span").textContent = "Enter workspace";
            }
        });
    }

    async function initDashboard() {
        if (!document.body.classList.contains("dashboard-page")) return;

        $("#today-label").textContent = date(new Date(), false);

        try {
            const me = await request("/auth/me");
            if (!me.authenticated) {
                location.replace("admin-login.html");
                return;
            }
            const name = me.username || "Administrator";
            $("#profile-name").textContent = name;
            $("#profile-avatar").textContent = name[0].toUpperCase();
            await load();
        } catch (e) {
            toast(e.message, true);
        }

        // Event Listeners
        $("#refresh-button").addEventListener("click", () => load(true));

        $("#previous-page").addEventListener("click", () => {
            if (state.page > 0) {
                state.page--;
                load();
            }
        });

        $("#next-page").addEventListener("click", () => {
            if (state.page + 1 < state.totalPages) {
                state.page++;
                load();
            }
        });

        $("#status-filter").addEventListener("change", e => {
            state.status = e.target.value;
            state.page = 0;
            load();
        });

        $("#priority-filter").addEventListener("change", e => {
            state.priority = e.target.value;
            state.page = 0;
            load();
        });

        $("#clear-filters").addEventListener("click", () => {
            state.status = state.priority = state.query = "";
            state.page = 0;
            $("#status-filter").value = $("#priority-filter").value = $("#search-input").value = "";
            load();
        });

        let timer;
        $("#search-input").addEventListener("input", e => {
            clearTimeout(timer);
            timer = setTimeout(() => {
                state.query = e.target.value.trim().toLowerCase();
                render();
            }, 180);
        });

        $("#messages-body").addEventListener("click", rowAction);
        $("#close-dialog").addEventListener("click", () => $("#message-dialog").close());
        $("#close-reply").addEventListener("click", () => $("#reply-dialog").close());
        $("#reply-form").addEventListener("submit", sendReply);

        $("#reply-body").addEventListener("input", () => {
            $("#reply-length").textContent = $("#reply-body").value.length + " / 5000";
        });

        $("#logout-button").addEventListener("click", logout);

        document.addEventListener("keydown", e => {
            // Focus search shortcut
            if (e.key === "/" && !["INPUT", "TEXTAREA", "SELECT"].includes(document.activeElement.tagName) && !document.querySelector(".dialog[open]")) {
                e.preventDefault();
                $("#search-input").focus();
            }
            // Close dialogs shortcut
            if (e.key === "Escape") {
                $("#message-dialog").close();
                $("#reply-dialog").close();
            }
        });
    }

    // --- Data Loading & Rendering ---
    async function load(notice = false) {
        $("#messages-body").innerHTML = '<tr><td colspan="6" class="table-state">Loading your inbox…</td></tr>';
        $("#refresh-button").disabled = true;

        try {
            const q = new URLSearchParams({
                page: String(state.page),
                size: String(state.size),
                sortBy: "createdAt"
            });

            if (state.status) q.set("status", state.status);
            if (state.priority) q.set("priority", state.priority);

            const p = await request("/admin/contacts?" + q);
            const d = p.data || {};

            state.messages = Array.isArray(d.content) ? d.content : [];
            state.total = Number(d.totalElement ?? state.messages.length);
            state.totalPages = Number(d.totalPage ?? (state.total ? 1 : 0));

            render();
            stats();
            if (notice) toast("Inbox refreshed.");
        } catch (e) {
            $("#messages-body").innerHTML = `<tr><td colspan="6" class="table-state">${esc(e.message)}</td></tr>`;
            toast(e.message, true);
        } finally {
            $("#refresh-button").disabled = false;
        }
    }

    function stats() {
        $("#stat-total").textContent = state.total;
        $("#stat-open").textContent = state.messages.filter(m => ["NEW", "PENDING"].includes(m.status)).length;
        $("#stat-replied").textContent = state.messages.filter(m => m.status === "REPLIED").length;
        $("#stat-critical").textContent = state.messages.filter(m => m.priority === "CRITICAL").length;
        $("#sidebar-count").textContent = state.total;
    }

    function render() {
        const rows = state.messages.filter(m =>
            !state.query || [m.name, m.email, m.message, m.adminNotes].join(" ").toLowerCase().includes(state.query)
        );

        $("#result-count").textContent = "(" + state.total + ")";

        $("#page-summary").textContent = state.total
            ? `Showing page ${state.page + 1} of ${Math.max(state.totalPages, 1)} · ${state.total} total`
            : "No messages yet";

        $("#page-number").textContent = state.page + 1;
        $("#previous-page").disabled = state.page <= 0;
        $("#next-page").disabled = !state.totalPages || state.page + 1 >= state.totalPages;

        if (!rows.length) {
            const emptyStateMsg = (state.query || state.status || state.priority)
                ? "No messages match these filters."
                : "Your inbox is clear. New enquiries will appear here.";
            $("#messages-body").innerHTML = `<tr><td colspan="6" class="table-state">${emptyStateMsg}</td></tr>`;
            return;
        }

        $("#messages-body").innerHTML = rows.map(m => {
            const initials = (m.name || "?").trim().split(/\s+/).slice(0, 2).map(x => x[0]).join("").toUpperCase();
            const messagePreview = esc((m.message || "").replace(/\s+/g, " ").trim() || "No message content");

            return `
        <tr data-id="${esc(m.id)}">
          <td>
            <div class="contact">
              <span class="avatar">${esc(initials)}</span>
              <span>
                <strong>${esc(m.name || "Unknown")}</strong>
                <small>${esc(m.email || "")}</small>
              </span>
            </div>
          </td>
          <td><span class="preview">${messagePreview}</span></td>
          <td><span class="pill priority ${cls(m.priority)}">${esc(m.priority || "—")}</span></td>
          <td><span class="pill status status-${cls(m.status)}">${esc(m.status || "UNKNOWN")}</span></td>
          <td class="received">${esc(date(m.createdAt, false))}</td>
          <td>
            <div class="row-actions">
              <button class="row-action" data-action="view" title="View" aria-label="View">
                <i class="ri-arrow-up-right-line"></i>
              </button>
              <button class="row-action" data-action="reply" title="Reply" aria-label="Reply">
                <i class="ri-reply-line"></i>
              </button>
              <button class="row-action delete" data-action="delete" title="Delete" aria-label="Delete">
                <i class="ri-delete-bin-line"></i>
              </button>
            </div>
          </td>
        </tr>`;
        }).join("");
    }

    // --- Row Actions ---
    async function rowAction(e) {
        const b = e.target.closest("button[data-action]");
        if (!b) return;

        const row = b.closest("tr");
        const m = state.messages.find(x => String(x.id) === row.dataset.id);
        if (!m) return;

        if (b.dataset.action === "view") return view(m);
        if (b.dataset.action === "reply") return openReply(m);

        if (b.dataset.action === "delete") {
            if (!confirm(`Permanently delete this message from ${m.name || "this contact"}? This cannot be undone.`)) return;
            try {
                await request("/admin/contacts/" + encodeURIComponent(m.id), { method: "DELETE" });
                toast("Message deleted.");
                await load();
            } catch (err) {
                toast(err.message, true);
            }
        }
    }

    async function view(m) {
        state.selected = m;

        // Auto-mark as read if new
        if (m.status === "NEW") {
            try {
                const p = await request("/admin/contacts/" + encodeURIComponent(m.id) + "/status", {
                    method: "PATCH",
                    body: JSON.stringify({ newStatus: "READ", note: "Opened in admin inbox" })
                });
                if (p.data) Object.assign(m, p.data);
            } catch (e) {
                toast("Opened, but status was not updated: " + e.message, true);
            }
        }

        $("#detail-name").textContent = m.name || "Conversation";

        $("#detail-content").innerHTML = `
      <a class="detail-email" href="mailto:${esc(m.email || "")}">${esc(m.email || "No email")}</a>
      <p class="detail-message">${esc(m.message || "No message content")}</p>
      <div class="detail-meta">
        <div><small>Status</small><strong>${esc(m.status || "—")}</strong></div>
        <div><small>Priority</small><strong>${esc(m.priority || "—")} · score ${esc(m.priorityScore ?? "—")}</strong></div>
        <div><small>Received</small><strong>${esc(date(m.createdAt))}</strong></div>
        <div><small>Last updated</small><strong>${esc(date(m.updatedAt))}</strong></div>
        <div><small>Replied at</small><strong>${esc(date(m.repliedAt))}</strong></div>
        <div><small>Admin note</small><strong>${esc(m.adminNotes || "No notes")}</strong></div>
      </div>
    `;

        const actions = $("#detail-actions");
        actions.innerHTML = `
      <button class="primary" id="detail-reply">
        <span>Reply to message</span><i class="ri-reply-line"></i>
      </button>
      ${m.status !== "ARCHIVED" ? `<button class="secondary" id="detail-archive"><i class="ri-archive-line"></i> Archive</button>` : ""}
    `;

        $("#detail-reply").addEventListener("click", () => {
            $("#message-dialog").close();
            openReply(m);
        });

        $("#detail-archive")?.addEventListener("click", async () => {
            try {
                const p = await request("/admin/contacts/" + encodeURIComponent(m.id) + "/status", {
                    method: "PATCH",
                    body: JSON.stringify({ newStatus: "ARCHIVED", note: "Archived from admin inbox" })
                });
                if (p.data) Object.assign(m, p.data);
                $("#message-dialog").close();
                toast("Conversation archived.");
                await load();
            } catch (e) {
                toast(e.message, true);
            }
        });

        $("#message-dialog").showModal();
        await load();
    }

    function openReply(m) {
        state.selected = m;
        $("#reply-recipient").textContent = "To: " + (m.name || "Contact") + " · " + (m.email || "");
        $("#reply-subject").value = "Re: Your enquiry";
        $("#reply-body").value = "";
        $("#reply-length").textContent = "0 / 5000";
        $("#reply-error").textContent = "";
        $("#reply-dialog").showModal();
    }

    async function sendReply(e) {
        e.preventDefault();
        if (!state.selected) return;

        const b = $("#send-reply");
        $("#reply-error").textContent = "";
        b.disabled = true;
        b.querySelector("span").textContent = "Sending…";

        try {
            const p = await request("/admin/contacts/" + encodeURIComponent(state.selected.id) + "/reply", {
                method: "POST",
                body: JSON.stringify({
                    message: $("#reply-subject").value.trim(),
                    body: $("#reply-body").value.trim()
                })
            });

            if (p.data) Object.assign(state.selected, p.data);

            $("#reply-dialog").close();
            toast("Reply sent successfully.");
            await load();
        } catch (err) {
            $("#reply-error").textContent = err.message || "Reply could not be sent.";
        } finally {
            b.disabled = false;
            b.querySelector("span").textContent = "Send reply";
        }
    }

    async function logout() {
        const b = $("#logout-button");
        b.disabled = true;
        try {
            await request("/auth/logout", { method: "POST", body: "{}" });
            location.replace("admin-login.html");
        } catch (e) {
            toast(e.message, true);
            b.disabled = false;
        }
    }

    // --- Boot ---
    document.addEventListener("DOMContentLoaded", async () => {
        if ($("#year")) $("#year").textContent = new Date().getFullYear();
        await initLogin();
        await initDashboard();
    });
})();
