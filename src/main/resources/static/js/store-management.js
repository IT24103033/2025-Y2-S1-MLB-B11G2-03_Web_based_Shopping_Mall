const API_BASE = "/api/stores";

let allStores = [];

// API helpers
async function fetchStores() {
  const res = await fetch(API_BASE);
  if (!res.ok) throw new Error("Failed to fetch stores");
  return await res.json();
}

async function createStore(store) {
  const res = await fetch(API_BASE, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(store),
  });
  if (!res.ok) {
    let msg = "Failed to create store";
    try { const data = await res.json(); throw { type: 'validation', data }; } catch (e) {
      if (e && e.type === 'validation') throw e; else throw new Error(msg);
    }
  }
  return await res.json();
}

async function updateStore(id, store) {
  const res = await fetch(`${API_BASE}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(store),
  });
  if (!res.ok) {
    let msg = "Failed to update store";
    try { const data = await res.json(); throw { type: 'validation', data }; } catch (e) {
      if (e && e.type === 'validation') throw e; else throw new Error(msg);
    }
  }
  return await res.json();
}

async function deleteStore(id) {
  const res = await fetch(`${API_BASE}/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Failed to delete store");
}

// UI helpers
function toast(message, type = "success") {
  const container = document.getElementById("toast-container");
  const el = document.createElement("div");
  el.className = `toast ${type}`;
  el.textContent = message;
  container.appendChild(el);
  setTimeout(() => { el.remove(); }, 3000);
}

function openModal(mode = "create", store = null) {
  const modal = document.getElementById("store-modal");
  const title = document.getElementById("modal-title");
  const saveBtn = document.getElementById("save-btn");
  const form = document.getElementById("store-form");

  form.reset();
  document.getElementById("store-id").value = "";
  if (mode === "edit" && store) {
    title.textContent = "Edit Store";
    saveBtn.textContent = "Update";
    document.getElementById("store-id").value = store.storeId;
    document.getElementById("storeName").value = store.storeName || "";
    document.getElementById("category").value = store.category || "";
    document.getElementById("description").value = store.description || "";
  } else {
    title.textContent = "Create Store";
    saveBtn.textContent = "Save";
  }
  modal.classList.add("show");
}

function closeModal() { document.getElementById("store-modal").classList.remove("show"); }

function renderMetrics(list) {
  document.getElementById("metric-total").textContent = list.length;
  const cats = new Set(list.map(s => (s.category || '').trim()).filter(Boolean));
  document.getElementById("metric-categories").textContent = cats.size;
}

function renderTable(list) {
  const tbody = document.getElementById("stores-tbody");
  const empty = document.getElementById("empty-state");
  tbody.innerHTML = "";
  if (!list.length) { empty.style.display = "block"; return; } else { empty.style.display = "none"; }
  list.forEach((s, idx) => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${idx + 1}</td>
      <td class="row-title">${escapeHtml(s.storeName || "")}</td>
      <td><span class="badge">${escapeHtml(s.category || "-")}</span></td>
      <td>${escapeHtml(s.description || "")}</td>
      <td>
        <div class="actions">
          <button class="secondary" data-action="edit" data-id="${s.storeId}">Edit</button>
          <button class="danger" data-action="delete" data-id="${s.storeId}">Delete</button>
        </div>
      </td>`;
    tbody.appendChild(tr);
  });
}

function escapeHtml(str) {
  return str.replace(/[&<>"']/g, (c) => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;','\'':'&#39;'}[c]));
}

function applySearch() {
  const q = document.getElementById("search").value.toLowerCase().trim();
  const filtered = !q ? allStores : allStores.filter(s =>
    (s.storeName || '').toLowerCase().includes(q) ||
    (s.category || '').toLowerCase().includes(q) ||
    (s.description || '').toLowerCase().includes(q)
  );
  renderMetrics(filtered);
  renderTable(filtered);
}

async function load() {
  try {
    allStores = await fetchStores();
    renderMetrics(allStores);
    renderTable(allStores);
  } catch (e) {
    toast(e.message || "Failed to load", "error");
  }
}

// Event wiring
document.getElementById("open-create").addEventListener("click", () => openModal("create"));
document.getElementById("close-modal").addEventListener("click", closeModal);
document.getElementById("cancel-modal").addEventListener("click", closeModal);
document.getElementById("search").addEventListener("input", applySearch);

document.getElementById("stores-tbody").addEventListener("click", async (e) => {
  const btn = e.target.closest("button");
  if (!btn) return;
  const action = btn.getAttribute("data-action");
  const id = btn.getAttribute("data-id");
  const store = allStores.find(s => String(s.storeId) === String(id));
  if (action === "edit") {
    openModal("edit", store);
  } else if (action === "delete") {
    if (confirm("Delete this store?")) {
      try { await deleteStore(id); toast("Store deleted"); await load(); } catch (err) { toast(err.message, "error"); }
    }
  }
});

document.getElementById("store-form").addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = document.getElementById("store-id").value;
  const payload = {
    storeName: document.getElementById("storeName").value.trim(),
    category: document.getElementById("category").value.trim(),
    description: document.getElementById("description").value.trim(),
  };
  // Client-side validation
  const errors = {};
  if (!payload.storeName) errors.storeName = "Store name is required";
  if (payload.storeName && payload.storeName.length < 3) errors.storeName = "Store name must be at least 3 characters";
  if (payload.category && payload.category.length > 50) errors.category = "Category must be at most 50 characters";
  if (payload.description && payload.description.length > 255) errors.description = "Description must be at most 255 characters";

  // Render validation errors
  const rowStore = document.getElementById("storeName").closest('.form-row');
  const rowCat = document.getElementById("category").closest('.form-row');
  const rowDesc = document.getElementById("description").closest('.form-row');
  rowStore.classList.toggle('invalid', !!errors.storeName);
  rowCat.classList.toggle('invalid', !!errors.category);
  rowDesc.classList.toggle('invalid', !!errors.description);
  document.getElementById("error-storeName").textContent = errors.storeName || "";
  document.getElementById("error-category").textContent = errors.category || "";
  document.getElementById("error-description").textContent = errors.description || "";

  const firstErrorField = errors.storeName ? document.getElementById("storeName") : errors.category ? document.getElementById("category") : errors.description ? document.getElementById("description") : null;
  if (firstErrorField) { firstErrorField.focus(); return; }

  try {
    if (id) { await updateStore(id, payload); toast("Store updated"); }
    else { await createStore(payload); toast("Store created"); }
    closeModal();
    await load();
  } catch (err) {
    if (err && err.type === 'validation') {
      const errs = err.data.errors || {};
      const rowStore = document.getElementById("storeName").closest('.form-row');
      const rowCat = document.getElementById("category").closest('.form-row');
      const rowDesc = document.getElementById("description").closest('.form-row');
      rowStore.classList.toggle('invalid', !!errs.storeName);
      rowCat.classList.toggle('invalid', !!errs.category);
      rowDesc.classList.toggle('invalid', !!errs.description);
      document.getElementById("error-storeName").textContent = errs.storeName || "";
      document.getElementById("error-category").textContent = errs.category || "";
      document.getElementById("error-description").textContent = errs.description || "";
      (errs.storeName && document.getElementById("storeName").focus()) || (errs.category && document.getElementById("category").focus()) || (errs.description && document.getElementById("description").focus());
    } else {
      toast(err.message || "Request failed", "error");
    }
  }
});

// Initial load
load();
