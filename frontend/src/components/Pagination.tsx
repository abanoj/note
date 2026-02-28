import { ChevronLeft, ChevronRight } from "lucide-react";

interface Props {
  page: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export default function Pagination({ page, totalPages, onPageChange }: Props) {
  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-3 mt-6">
      <button
        onClick={() => onPageChange(page - 1)}
        disabled={page === 0}
        className="btn-secondary flex items-center gap-1 px-3 py-1.5 text-sm font-medium rounded-lg disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
      >
        <ChevronLeft className="w-4 h-4" />
        Anterior
      </button>
      <span className="text-sm text-gray-500">
        Pagina {page + 1} de {totalPages}
      </span>
      <button
        onClick={() => onPageChange(page + 1)}
        disabled={page >= totalPages - 1}
        className="btn-secondary flex items-center gap-1 px-3 py-1.5 text-sm font-medium rounded-lg disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
      >
        Siguiente
        <ChevronRight className="w-4 h-4" />
      </button>
    </div>
  );
}
