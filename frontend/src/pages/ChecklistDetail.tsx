import { useEffect, useState, useCallback } from "react";
import { useParams, Link } from "react-router-dom";
import {
  ArrowLeft,
  Plus,
  Loader2,
  Inbox,
  Filter,
} from "lucide-react";
import toast from "react-hot-toast";
import * as checklistsApi from "../api/checklists";
import * as itemsApi from "../api/items";
import type {
  ChecklistResponseDto,
  ItemDto,
  ItemStatus,
  ItemPriority,
} from "../types";
import ItemRow from "../components/ItemRow";
import CreateItemModal from "../components/CreateItemModal";
import ConfirmDialog from "../components/ConfirmDialog";

type StatusFilter = "ALL" | ItemStatus;

export default function ChecklistDetail() {
  const { id } = useParams<{ id: string }>();
  const checklistId = Number(id);

  const [checklist, setChecklist] = useState<ChecklistResponseDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [deleteItemId, setDeleteItemId] = useState<number | null>(null);
  const [statusFilter, setStatusFilter] = useState<StatusFilter>("ALL");

  const fetchChecklist = useCallback(async () => {
    try {
      const { data } = await checklistsApi.getChecklist(checklistId);
      setChecklist(data);
    } catch {
      toast.error("Error al cargar el checklist");
    } finally {
      setLoading(false);
    }
  }, [checklistId]);

  useEffect(() => {
    fetchChecklist();
  }, [fetchChecklist]);

  const handleCreateItem = async (title: string, priority: ItemPriority) => {
    try {
      await itemsApi.createItem(checklistId, {
        title,
        status: "PENDING",
        priority,
      });
      toast.success("Item creado");
      setModalOpen(false);
      fetchChecklist();
    } catch {
      toast.error("Error al crear el item");
    }
  };

  const handleStatusChange = async (item: ItemDto, status: ItemStatus) => {
    if (!item.id) return;
    try {
      await itemsApi.updateItem(checklistId, item.id, { ...item, status });
      fetchChecklist();
    } catch {
      toast.error("Error al actualizar el item");
    }
  };

  const handlePriorityChange = async (
    item: ItemDto,
    priority: ItemPriority
  ) => {
    if (!item.id) return;
    try {
      await itemsApi.updateItem(checklistId, item.id, { ...item, priority });
      fetchChecklist();
    } catch {
      toast.error("Error al actualizar el item");
    }
  };

  const handleTitleChange = async (item: ItemDto, title: string) => {
    if (!item.id) return;
    try {
      await itemsApi.updateItem(checklistId, item.id, { ...item, title });
      fetchChecklist();
    } catch {
      toast.error("Error al actualizar el item");
    }
  };

  const handleDeleteItem = async () => {
    if (deleteItemId === null) return;
    try {
      await itemsApi.deleteItem(checklistId, deleteItemId);
      toast.success("Item eliminado");
      setDeleteItemId(null);
      fetchChecklist();
    } catch {
      toast.error("Error al eliminar el item");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-violet-600" />
      </div>
    );
  }

  if (!checklist) {
    return (
      <div className="text-center py-20">
        <p className="text-gray-500">Checklist no encontrado</p>
        <Link to="/dashboard" className="text-violet-600 text-sm mt-2 inline-block">
          Volver al inicio
        </Link>
      </div>
    );
  }

  const filteredItems =
    statusFilter === "ALL"
      ? checklist.items
      : checklist.items.filter((i) => i.status === statusFilter);

  const progress = checklist.progress ?? 0;
  const progressPercent = Math.round(progress * 100);

  return (
    <div>
      <Link
        to="/dashboard"
        className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-violet-600 mb-4 transition-colors"
      >
        <ArrowLeft className="w-4 h-4" />
        Volver al inicio
      </Link>

      <div className="flex items-start justify-between mb-2">
        <h1 className="text-xl font-bold text-gradient">{checklist.title}</h1>
        <button
          onClick={() => setModalOpen(true)}
          className="flex items-center gap-1.5 btn-primary px-3 py-1.5 rounded-lg text-sm font-medium shrink-0 cursor-pointer"
        >
          <Plus className="w-4 h-4" />
          Nuevo item
        </button>
      </div>

      {checklist.numberOfItems > 0 && (
        <div className="mb-6">
          <div className="flex items-center justify-between text-xs text-gray-500 mb-1">
            <span>
              {checklist.items.filter((i) => i.status === "DONE").length} de{" "}
              {checklist.numberOfItems} completados
            </span>
            <span>{progressPercent}%</span>
          </div>
          <div className="w-full bg-gray-100 rounded-full h-2">
            <div
              className="h-2 rounded-full bg-gradient-to-r from-violet-500 to-indigo-500 transition-all duration-300"
              style={{ width: `${progressPercent}%` }}
            />
          </div>
        </div>
      )}

      {checklist.numberOfItems > 0 && (
        <div className="flex items-center gap-2 mb-4">
          <Filter className="w-4 h-4 text-gray-400" />
          {(
            [
              ["ALL", "Todos"],
              ["PENDING", "Pendientes"],
              ["IN_PROGRESS", "En progreso"],
              ["DONE", "Completados"],
            ] as [StatusFilter, string][]
          ).map(([value, label]) => (
            <button
              key={value}
              onClick={() => setStatusFilter(value)}
              className={`px-2.5 py-1 text-xs rounded-full font-medium transition-colors cursor-pointer ${
                statusFilter === value
                  ? "bg-violet-100 text-violet-700"
                  : "bg-gray-100 text-gray-500 hover:bg-gray-200"
              }`}
            >
              {label}
            </button>
          ))}
        </div>
      )}

      {checklist.numberOfItems === 0 ? (
        <div className="text-center py-16">
          <Inbox className="w-12 h-12 text-gray-300 mx-auto mb-3" />
          <p className="text-gray-500 text-sm">
            Este checklist esta vacio. Anade tu primer item!
          </p>
        </div>
      ) : filteredItems.length === 0 ? (
        <div className="text-center py-16">
          <p className="text-gray-400 text-sm">
            No hay items con este filtro.
          </p>
        </div>
      ) : (
        <div className="space-y-2">
          {filteredItems.map((item) => (
            <ItemRow
              key={item.id}
              item={item}
              onStatusChange={handleStatusChange}
              onPriorityChange={handlePriorityChange}
              onTitleChange={handleTitleChange}
              onDelete={setDeleteItemId}
            />
          ))}
        </div>
      )}

      <CreateItemModal
        open={modalOpen}
        onClose={() => setModalOpen(false)}
        onSubmit={handleCreateItem}
      />

      <ConfirmDialog
        open={deleteItemId !== null}
        title="Eliminar item"
        message="Esta accion no se puede deshacer."
        onConfirm={handleDeleteItem}
        onCancel={() => setDeleteItemId(null)}
      />
    </div>
  );
}
