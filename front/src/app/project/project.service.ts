import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/projects'; // URL du backend

  constructor(private http: HttpClient) {}

  // Créer un projet avec adminId
  createProject(projectData: { name: string; description: string; startDate: string; adminId: number }): Observable<any> {
    return this.http.post<any>(this.apiUrl, projectData);
  }

  // Récupérer tous les projets
  getProjects(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getProjectsByAdmin(adminId : number) : Observable<any[]> {
    return this.http.get<any>(`${this.apiUrl}/admin/${adminId}`);
  }

  getProjectsByUser(userId : number) : Observable<any[]> {
    return this.http.get<any>(`${this.apiUrl}/user/${userId}`);
  }

    // Récupérer un projet par son ID
  getProjectById(projectId: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/${projectId}`);
  }
    
      // Ajouter un membre à un projet par son email
  addMemberToProject(projectId: number, email: string, role : string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/${projectId}/add-member?email=${email}&role=${role}`, {});
  }

  createTask(projectId: number, taskData: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/${projectId}/tasks`, taskData);
  }
}
