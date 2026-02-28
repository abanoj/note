export interface AuthenticationRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstname: string;
  lastname: string;
  email: string;
  password: string;
}

export interface AuthenticationResponse {
  access_token: string;
  refresh_token: string;
}

// Checklist / Item types

export type ItemStatus = "PENDING" | "IN_PROGRESS" | "DONE";
export type ItemPriority = "LOW" | "MEDIUM" | "HIGH";

export interface ItemDto {
  id?: number;
  title: string;
  status: ItemStatus;
  priority: ItemPriority;
}

export interface ChecklistCreateRequestDto {
  title: string;
}

export interface ChecklistUpdateRequestDto {
  id: number;
  title: string;
}

export interface ChecklistResponseDto {
  id: number;
  title: string;
  numberOfItems: number;
  progress: number | null;
  items: ItemDto[];
}

// TextNote types

export interface TextNoteCreateRequestDto {
  title: string;
  content?: string;
}

export interface TextNoteUpdateRequestDto {
  id: number;
  title: string;
  content?: string;
}

export interface TextNoteResponseDto {
  id: number;
  title: string;
  content: string;
}

// Pagination

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

// Error types

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
}

export interface ValidationErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  messages: string[];
  path: string;
}
