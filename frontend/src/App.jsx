import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ToastProvider } from './components/Toast';
import Layout from './components/Layout';
import { useAuth } from './context/AuthContext';
import Dashboard from './pages/Dashboard';
import LoginPage from './pages/Auth/LoginPage';
import RegisterPage from './pages/Auth/RegisterPage';
import EtudiantList from './pages/Etudiants/EtudiantList';
import EtudiantForm from './pages/Etudiants/EtudiantForm';
import ProfesseurList from './pages/Professeurs/ProfesseurList';
import ProfesseurForm from './pages/Professeurs/ProfesseurForm';
import CoursList from './pages/Cours/CoursList';
import CoursForm from './pages/Cours/CoursForm';
import SalleList from './pages/Salles/SalleList';
import SalleForm from './pages/Salles/SalleForm';
import InscriptionList from './pages/Inscriptions/InscriptionList';
import InscriptionForm from './pages/Inscriptions/InscriptionForm';
import NoteList from './pages/Notes/NoteList';
import NoteForm from './pages/Notes/NoteForm';
import GradeList from './pages/Grades/GradeList';
import GradeForm from './pages/Grades/GradeForm';

function ProtectedRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading">Chargement...</div>;
  return user ? children : <Navigate to="/login" replace />;
}

function PublicRoute({ children }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="loading">Chargement...</div>;
  return user ? <Navigate to="/" replace /> : children;
}

export default function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
      <Routes>
        <Route path="/login" element={<PublicRoute><LoginPage /></PublicRoute>} />
        <Route path="/register" element={<PublicRoute><RegisterPage /></PublicRoute>} />
        <Route element={<ProtectedRoute><Layout /></ProtectedRoute>}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/etudiants" element={<EtudiantList />} />
          <Route path="/etudiants/nouveau" element={<EtudiantForm />} />
          <Route path="/etudiants/:id" element={<EtudiantForm />} />
          <Route path="/professeurs" element={<ProfesseurList />} />
          <Route path="/professeurs/nouveau" element={<ProfesseurForm />} />
          <Route path="/professeurs/:id" element={<ProfesseurForm />} />
          <Route path="/cours" element={<CoursList />} />
          <Route path="/cours/nouveau" element={<CoursForm />} />
          <Route path="/cours/:id" element={<CoursForm />} />
          <Route path="/salles" element={<SalleList />} />
          <Route path="/salles/nouveau" element={<SalleForm />} />
          <Route path="/salles/:id" element={<SalleForm />} />
          <Route path="/inscriptions" element={<InscriptionList />} />
          <Route path="/inscriptions/nouveau" element={<InscriptionForm />} />
          <Route path="/inscriptions/:id" element={<InscriptionForm />} />
          <Route path="/notes" element={<NoteList />} />
          <Route path="/notes/nouveau" element={<NoteForm />} />
          <Route path="/notes/:id" element={<NoteForm />} />
          <Route path="/grades" element={<GradeList />} />
          <Route path="/grades/nouveau" element={<GradeForm />} />
          <Route path="/grades/:id" element={<GradeForm />} />
        </Route>
      </Routes>
      </ToastProvider>
    </BrowserRouter>
  );
}
