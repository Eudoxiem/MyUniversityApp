import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
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

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
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
    </BrowserRouter>
  );
}
