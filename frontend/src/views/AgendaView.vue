<template>
  <div class="agenda-page">

    <div class="agenda-card">

      <div class="agenda-toolbar">
        <h2>Agenda</h2>
      </div>

      <div class="agenda-calendar">
        <FullCalendar
          ref="calendarRef"
          :options="calendarOptions"
        />
      </div>

      <div
        v-if="modalAberto && eventoSelecionado"
        class="agenda-modal-overlay"
        @click.self="modalAberto = false"
      >

        <div class="agenda-modal">

          <div class="agenda-modal-header">
            <h2>
              {{
                eventoSelecionado.tipo === 'SOLICITACAO'
                  ? 'Detalhes da solicitação'
                  : 'Detalhes do agendamento'
              }}
            </h2>

            <button
              class="agenda-modal-close"
              @click="modalAberto = false"
            >
              ×
            </button>
          </div>

          <div class="agenda-modal-content">

            <div class="agenda-detail">
              <span>Cliente</span>
              <strong>{{ eventoSelecionado.clienteNome }}</strong>
            </div>

            <div class="agenda-detail">
              <span>Telefone</span>
              <strong>{{ eventoSelecionado.clienteTelefone }}</strong>
            </div>

            <div class="agenda-detail">
              <span>Procedimento</span>
              <strong>{{ eventoSelecionado.procedimentoNome }}</strong>
            </div>

            <div class="agenda-detail">
              <span>Data</span>
              <strong>{{ eventoSelecionado.data }}</strong>
            </div>

            <div class="agenda-detail">
              <span>Horário</span>
              <strong>{{ eventoSelecionado.hora }}</strong>
            </div>

            <div
              v-if="eventoSelecionado.observacao"
              class="agenda-detail"
            >
              <span>Observação</span>
              <strong>{{ eventoSelecionado.observacao }}</strong>
            </div>

            <div class="agenda-modal-actions">

              <template v-if="eventoSelecionado.tipo === 'SOLICITACAO'">

                <button
                  class="agenda-button agenda-button-cancel"
                  @click="cancelarSolicitacao"
                >
                  Cancelar solicitação
                </button>

                <button
                  type="button"
                  class="agenda-button agenda-button-confirm"
                  @click="confirmarSolicitacao"
                >
                  Confirmar solicitação
                </button>
              </template>

              <template v-else>

                <button
                  class="agenda-button agenda-button-cancel"
                  @click="cancelarAgendamento"
                >
                  Cancelar agendamento
                </button>

              </template>

            </div>

          </div>

        </div>

      </div>

    </div>

  </div>
</template>

<script setup lang="ts">
import { useToast } from 'vue-toastification'
import { onMounted, ref } from 'vue'
import FullCalendar from '@fullcalendar/vue3'
import timeGridPlugin from '@fullcalendar/vue3/timegrid'
import type { CalendarOptions, EventInput } from '@fullcalendar/vue3'

import themePlugin from '@fullcalendar/vue3/themes/classic'
import '@fullcalendar/vue3/skeleton.css'
import '@fullcalendar/vue3/themes/classic/theme.css'
import '@fullcalendar/vue3/themes/classic/palette.css'

import api from '@/services/api'

interface AgendaEvento {
  id: number
  tipo: 'SOLICITACAO' | 'AGENDAMENTO'
  data: string
  hora: string
  observacao: string | null
  status: string
  clienteId: number
  clienteNome: string
  clienteTelefone: string
  procedimentoId: number
  procedimentoNome: string
}

interface EventoSelecionado {
  id: number
  tipo: 'SOLICITACAO' | 'AGENDAMENTO'
  status: string
  observacao: string | null
  clienteNome: string
  clienteTelefone: string
  procedimentoNome: string
  data: string
  hora: string
}
const toast = useToast()
const modalAberto = ref(false)

const eventoSelecionado = ref<EventoSelecionado | null>(null)

function aoClicarNoEvento(info: any) {
  const evento = info.event
  const dados = evento.extendedProps

  console.log('=== EVENTO CLICADO ===')
  console.log('ID FullCalendar:', evento.id)
  console.log('Dados:', dados)

  eventoSelecionado.value = {
    id: Number(evento.id.split('-')[1]),
    tipo: dados.tipo,
    status: dados.status,
    observacao: dados.observacao,
    clienteNome: dados.clienteNome,
    clienteTelefone: dados.clienteTelefone,
    procedimentoNome: dados.procedimentoNome,
    data: evento.startStr.split('T')[0],
    hora: evento.startStr.split('T')[1]?.substring(0, 5) ?? ''
  }

  console.log('Evento selecionado:', eventoSelecionado.value)

  modalAberto.value = true
}

async function confirmarSolicitacao() {
  if (!eventoSelecionado.value) {
    return
  }
  const solicitacaoId = eventoSelecionado.value.id
  try {
    await api.patch(
      `/solicitacoes/${solicitacaoId}/aprovar`
    )
    modalAberto.value = false
    await carregarEventos()
    toast.success('Solicitação confirmada! O agendamento foi criado.')
  } catch (error) {
    console.error('Erro ao confirmar solicitação:', error)
    toast.error('Não foi possível confirmar a solicitação.')
  }
}

async function cancelarSolicitacao() {
  if (!eventoSelecionado.value) {
    return
  }
  const solicitacaoId = eventoSelecionado.value.id
  try {
    await api.patch(
      `/solicitacoes/${solicitacaoId}/cancelar`
    )
    modalAberto.value = false
    await carregarEventos()
    toast.success('Solicitação cancelada.')
  } catch (error) {
    console.error('Erro ao cancelar solicitação:', error)
    toast.error('Não foi possível cancelar a solicitação.')
  }
}

async function cancelarAgendamento() {
  if (!eventoSelecionado.value) {
    return
  }
  const agendamentoId = eventoSelecionado.value.id
  try {
    await api.patch(
      `/agendamentos/${agendamentoId}/cancelar`
    )
    modalAberto.value = false
    await carregarEventos()
    toast.success('Agendamento cancelado.')
  } catch (error) {
    console.error('Erro ao cancelar agendamento:', error)
    toast.error('Não foi possível cancelar o agendamento.')
  }
}

function aoPassarMouseNoEvento(info: any) {
  const tipo = info.event.extendedProps.tipo

  if (tipo === 'SOLICITACAO') {
    info.el.setAttribute('title', 'Solicitação pendente')
  } else if (tipo === 'AGENDAMENTO') {
    info.el.setAttribute('title', 'Agendamento confirmado')
  }
}

const eventos = ref<EventInput[]>([])
const calendarRef = ref()

const calendarOptions = ref<CalendarOptions>({
  plugins: [timeGridPlugin, themePlugin],
  initialView: 'timeGridWeek',
  locale: 'pt-br',
  firstDay: 0,
  slotMinTime: '06:00:00',
  slotMaxTime: '22:00:00',
  headerToolbar: {
    left: 'prev',
    center: 'title',
    right: 'next'
  },
  eventTimeFormat: {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
  },
  eventClick: aoClicarNoEvento,
  eventMouseEnter: aoPassarMouseNoEvento,
  height: 'auto'
})

async function carregarEventos() {
  try {

    const [solicitacoesResponse, agendamentosResponse] =
      await Promise.all([
        api.get<AgendaEvento[]>('/solicitacoes/pendentes/agenda'),
        api.get<AgendaEvento[]>('/agendamentos/confirmados')
      ])

    const solicitacoes = solicitacoesResponse.data
    const agendamentos = agendamentosResponse.data

    const eventosSolicitacoes: EventInput[] = solicitacoes.map(evento => ({
      id: `solicitacao-${evento.id}`,
      title: `${evento.procedimentoNome} - ${evento.clienteNome}`,
      start: `${evento.data}T${evento.hora}`,
      className: 'evento-solicitacao',
      extendedProps: {
        tipo: evento.tipo,
        status: evento.status,
        observacao: evento.observacao,
        clienteId: evento.clienteId,
        clienteNome: evento.clienteNome,
        clienteTelefone: evento.clienteTelefone,
        procedimentoId: evento.procedimentoId,
        procedimentoNome: evento.procedimentoNome
      }
    }))

    const eventosAgendamentos: EventInput[] = agendamentos.map(evento => ({
      id: `agendamento-${evento.id}`,
      title: `${evento.procedimentoNome} - ${evento.clienteNome}`,
      start: `${evento.data}T${evento.hora}`,
      className: 'evento-agendamento',
      extendedProps: {
        tipo: evento.tipo,
        status: evento.status,
        observacao: evento.observacao,
        clienteId: evento.clienteId,
        clienteNome: evento.clienteNome,
        clienteTelefone: evento.clienteTelefone,
        procedimentoId: evento.procedimentoId,
        procedimentoNome: evento.procedimentoNome
      }
    }))

    eventos.value = [
      ...eventosSolicitacoes,
      ...eventosAgendamentos
    ]

    const calendarApi = calendarRef.value?.getApi()

    if (calendarApi) {
      calendarApi.removeAllEvents()
      calendarApi.addEventSource(eventos.value)
    }

  } catch (error) {
    console.error('Erro ao carregar agenda:', error)
  }
}

onMounted(() => {
  carregarEventos()
})
</script>

<style scoped>
.agenda-page {
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.agenda-card {
  width: 100%;
  background: #ffffff;
  padding: 8px;
  box-sizing: border-box;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.22);
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow: hidden;
}

.agenda-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  flex-shrink: 0;
}

.agenda-toolbar h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #333333;
}

.agenda-calendar {
  width: 100%;
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overflow-x: hidden;
}

.agenda-modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 9999;

  display: flex;
  align-items: center;
  justify-content: center;

  background: rgba(0, 0, 0, 0.45);
}

.agenda-modal {
  width: 460px;
  max-width: calc(100% - 40px);

  background: #ffffff;
  border-radius: 16px;

  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);

  overflow: hidden;
}

.agenda-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;

  padding: 20px 24px;

  border-bottom: 1px solid #eeeeee;
}

.agenda-modal-header h2 {
  margin: 0;

  font-size: 20px;
  font-weight: 600;

  color: #333333;
}

.agenda-modal-close {
  border: none;
  background: transparent;

  font-size: 28px;
  line-height: 1;

  color: #777777;

  cursor: pointer;
}

.agenda-modal-close:hover {
  color: #333333;
}

.agenda-modal-content {
  display: flex;
  flex-direction: column;
  gap: 18px;

  padding: 24px;
}

.agenda-detail {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.agenda-detail span {
  font-size: 12px;
  font-weight: 600;

  color: #888888;
  text-transform: uppercase;
}

.agenda-detail strong {
  font-size: 15px;
  font-weight: 500;

  color: #333333;
}

.agenda-modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;

  padding: 0 24px 24px;
}

.agenda-button {
  border: none;
  border-radius: 8px;

  padding: 10px 16px;

  font-size: 14px;
  font-weight: 500;

  cursor: pointer;

  transition: 0.2s ease;
}

.agenda-button:hover {
  opacity: 0.85;
}

.agenda-button-cancel {
  background: #eeeeee;
  color: #555555;
}

.agenda-button-confirm {
  background: #dd81b5;
  color: white;
}

</style>

/* ============================= */
/* FULLCALENDAR                  */
/* ============================= */
<style>

.evento-solicitacao .fc-classic-lNM {
  border-color: #F3B562 !important;
}

.evento-agendamento .fc-classic-lNM {
  border-color: #8BC99B !important;
}

:deep(.fc) {
  width: 100%;
  font-family: inherit;
  color: #333333;
}

/* Barra superior */

:deep(.fc-toolbar) {
  margin-bottom: 20px !important;
}

/* Cor do título do mês */
.fc-classic-AVD.fc-classic-DIS {
  color: black;
}

/* Cor do botão que muda o mês */
.fc-classic-yi0 button {
  background-color: #dd81b5;
  border-color: #dd81b5;
}

.fc-classic-yi0 button:hover{
  background-color: #94335c;
  border-color: #94335c;
}

.fc-classic-hbn {
  background-color: #f0c5d6 !important;
}


:deep(.fc-toolbar-title) {
  font-size: 20px !important;
  font-weight: 600 !important;
  text-transform: capitalize;
  color: black !important;
}

:deep(.fc-button) {
  border: none !important;
  border-radius: 8px !important;
  padding: 8px 12px !important;
  font-size: 14px !important;
  font-weight: 500 !important;
  box-shadow: none !important;
  transition: 0.2s ease;
}

:deep(.fc-button:hover) {
  opacity: 0.85;
}

:deep(.fc-button:focus) {
  box-shadow: none !important;
}

:deep(.fc-col-header-cell) {
  background: #f8f8f8;
  border-color: #eeeeee !important;
}

:deep(.fc-col-header-cell-cushion) {
  padding: 12px 6px !important;
  font-size: 13px;
  font-weight: 600;
  text-decoration: none;
  text-transform: uppercase;
}

:deep(.fc-daygrid-day) {
  min-height: 110px;
  border-color: #eeeeee !important;
  background: #ffffff;
  transition: background 0.2s ease;
}

:deep(.fc-daygrid-day:hover) {
  background: #fafafa;
}

:deep(.fc-daygrid-day-number) {
  padding: 10px !important;
  font-size: 13px;
  font-weight: 500;
  text-decoration: none;
}

:deep(.fc-day-today) {
  background: rgba(245, 240, 235, 0.65) !important;
}

:deep(.fc-day-today .fc-daygrid-day-number) {
  font-weight: 700;
}

:deep(.fc-daygrid-event) {
  border: none !important;
  border-radius: 6px !important;
  margin: 3px 5px !important;
  padding: 5px 7px !important;
  cursor: pointer;
}

:deep(.fc-event-title) {
  font-size: 12px;
  font-weight: 500;
  white-space: normal;
}

:deep(.fc-event-time) {
  font-size: 11px;
  font-weight: 600;
}

:deep(.fc-more-link) {
  font-size: 12px;
  font-weight: 600;
  text-decoration: none;
}

:deep(.fc-scrollgrid) {
  border-radius: 10px;
  overflow: hidden;
}

:deep(.fc-scrollgrid td:last-of-type) {
  border-right: none;
}
</style>