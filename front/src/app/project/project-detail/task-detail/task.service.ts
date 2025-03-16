import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class TaskService {
  private baseUrl = 'http://localhost:8080/api/projects';

  constructor(private http: HttpClient) {}

  assignTask(taskId: number, assigneeId: number, projectId: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${projectId}/tasks/${taskId}/assign?assigneeId=${assigneeId}`, {});
  }

  updateTask(projectId: number, taskId: number, updates: any, user_id : number): Observable<any> {
    return this.http.patch(`${this.baseUrl}/${projectId}/tasks/${taskId}?userId=${user_id}`, updates);
  }
}
