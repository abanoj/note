import api from "./axios";
import type { ItemDto } from "../types";

export const getItems = (checklistId: number) =>
  api.get<ItemDto[]>(`/checklists/${checklistId}/items`);

export const getItem = (checklistId: number, itemId: number) =>
  api.get<ItemDto>(`/checklists/${checklistId}/items/${itemId}`);

export const createItem = (checklistId: number, data: Omit<ItemDto, "id">) =>
  api.post<ItemDto>(`/checklists/${checklistId}/items`, data);

export const updateItem = (checklistId: number, itemId: number, data: ItemDto) =>
  api.put<ItemDto>(`/checklists/${checklistId}/items/${itemId}`, data);

export const deleteItem = (checklistId: number, itemId: number) =>
  api.delete(`/checklists/${checklistId}/items/${itemId}`);
