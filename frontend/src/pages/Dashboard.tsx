import { useEffect, useState, useCallback } from "react";
import { Plus, Loader2, Inbox } from "lucide-react";
import toast from "react-hot-toast";
import * as checklistsApi from "../api/checklists";
import * as textNotesApi from "../api/textNotes";
import type { ChecklistResponseDto, TextNoteResponseDto } from "../types";
import ChecklistCard from "../components/ChecklistCard";
import TextNoteCard from "../components/TextNoteCard";
import CreateChecklistModal from "../components/CreateChecklistModal";
import CreateTextNoteModal from "../components/CreateTextNoteModal";
import ConfirmDialog from "../components/ConfirmDialog";

type DeleteTarget = { type: "checklist"; id: number } | { type: "note"; id: number } | null;

export default function Dashboard() {
  const [checklists, setChecklists] = useState<ChecklistResponseDto[]>([]);
  const [notes, setNotes] = useState<TextNoteResponseDto[]>([]);
  const [loading, setLoading] = useState(true);

  const [checklistModalOpen, setChecklistModalOpen] = useState(false);
  const [editingChecklist, setEditingChecklist] = useState<ChecklistResponseDto | null>(null);

  const [noteModalOpen, setNoteModalOpen] = useState(false);
  const [editingNote, setEditingNote] = useState<TextNoteResponseDto | null>(null);

  const [deleteTarget, setDeleteTarget] = useState<DeleteTarget>(null);

  const fetchData = useCallback(async () => {
    try {
      const [checklistsRes, notesRes] = await Promise.all([
        checklistsApi.getChecklists(),
        textNotesApi.getTextNotes(),
      ]);
      setChecklists(checklistsRes.data);
      setNotes(notesRes.data);
    } catch {
      toast.error("Error al cargar los datos");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  // Checklist handlers
  const handleChecklistSubmit = async (title: string) => {
    try {
      if (editingChecklist) {
        await checklistsApi.updateChecklist(editingChecklist.id, { id: editingChecklist.id, title });
        toast.success("Checklist actualizado");
      } else {
        await checklistsApi.createChecklist({ title });
        toast.success("Checklist creado");
      }
      setChecklistModalOpen(false);
      setEditingChecklist(null);
      fetchData();
    } catch {
      toast.error("Error al guardar el checklist");
    }
  };

  const openEditChecklist = (checklist: ChecklistResponseDto) => {
    setEditingChecklist(checklist);
    setChecklistModalOpen(true);
  };

  // TextNote handlers
  const handleNoteSubmit = async (title: string, content: string) => {
    try {
      if (editingNote) {
        await textNotesApi.updateTextNote(editingNote.id, { id: editingNote.id, title, content });
        toast.success("Nota actualizada");
      } else {
        await textNotesApi.createTextNote({ title, content });
        toast.success("Nota creada");
      }
      setNoteModalOpen(false);
      setEditingNote(null);
      fetchData();
    } catch {
      toast.error("Error al guardar la nota");
    }
  };

  const openEditNote = (note: TextNoteResponseDto) => {
    setEditingNote(note);
    setNoteModalOpen(true);
  };

  // Delete handler
  const handleDelete = async () => {
    if (!deleteTarget) return;
    try {
      if (deleteTarget.type === "checklist") {
        await checklistsApi.deleteChecklist(deleteTarget.id);
        toast.success("Checklist eliminado");
      } else {
        await textNotesApi.deleteTextNote(deleteTarget.id);
        toast.success("Nota eliminada");
      }
      setDeleteTarget(null);
      fetchData();
    } catch {
      toast.error("Error al eliminar");
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-6 h-6 animate-spin text-indigo-600" />
      </div>
    );
  }

  return (
    <div className="space-y-10">
      {/* Checklists section */}
      <section>
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-xl font-bold text-gray-900">Mis checklists</h1>
          <button
            onClick={() => {
              setEditingChecklist(null);
              setChecklistModalOpen(true);
            }}
            className="flex items-center gap-1.5 bg-indigo-600 text-white px-3 py-1.5 rounded-md text-sm font-medium hover:bg-indigo-700 transition-colors cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            Nuevo checklist
          </button>
        </div>

        {checklists.length === 0 ? (
          <div className="text-center py-12">
            <Inbox className="w-12 h-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-500 text-sm">
              No tienes checklists todavia. Crea uno para empezar!
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {checklists.map((checklist) => (
              <ChecklistCard
                key={checklist.id}
                checklist={checklist}
                onDelete={(id) => setDeleteTarget({ type: "checklist", id })}
                onEdit={openEditChecklist}
              />
            ))}
          </div>
        )}
      </section>

      {/* Text Notes section */}
      <section>
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-xl font-bold text-gray-900">Mis notas</h1>
          <button
            onClick={() => {
              setEditingNote(null);
              setNoteModalOpen(true);
            }}
            className="flex items-center gap-1.5 bg-amber-600 text-white px-3 py-1.5 rounded-md text-sm font-medium hover:bg-amber-700 transition-colors cursor-pointer"
          >
            <Plus className="w-4 h-4" />
            Nueva nota
          </button>
        </div>

        {notes.length === 0 ? (
          <div className="text-center py-12">
            <Inbox className="w-12 h-12 text-gray-300 mx-auto mb-3" />
            <p className="text-gray-500 text-sm">
              No tienes notas todavia. Crea una para empezar!
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            {notes.map((note) => (
              <TextNoteCard
                key={note.id}
                note={note}
                onDelete={(id) => setDeleteTarget({ type: "note", id })}
                onEdit={openEditNote}
              />
            ))}
          </div>
        )}
      </section>

      {/* Modals */}
      <CreateChecklistModal
        open={checklistModalOpen}
        editing={editingChecklist}
        onClose={() => {
          setChecklistModalOpen(false);
          setEditingChecklist(null);
        }}
        onSubmit={handleChecklistSubmit}
      />

      <CreateTextNoteModal
        open={noteModalOpen}
        editing={editingNote}
        onClose={() => {
          setNoteModalOpen(false);
          setEditingNote(null);
        }}
        onSubmit={handleNoteSubmit}
      />

      <ConfirmDialog
        open={deleteTarget !== null}
        title={deleteTarget?.type === "checklist" ? "Eliminar checklist" : "Eliminar nota"}
        message={
          deleteTarget?.type === "checklist"
            ? "Se eliminaran todos los items de este checklist. Esta accion no se puede deshacer."
            : "Esta accion no se puede deshacer."
        }
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}
