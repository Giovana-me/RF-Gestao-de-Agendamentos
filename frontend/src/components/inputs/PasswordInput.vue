<template>
    <div class="password-input">
        <input
            :type="mostrarSenha ? 'text' : 'password'"
            :placeholder="placeholder"
            :value="modelValue"
            :disabled="disabled"
            @input="atualizarValor"
        />

        <button
            type="button"
            class="toggle-password"
            @click="mostrarSenha = !mostrarSenha"
        >
            <Eye
                v-if="!mostrarSenha"
                :size="20"
            />

            <EyeOff
                v-else
                :size="20"
            />
        </button>
    </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Eye, EyeOff } from "lucide-vue-next";

const props = defineProps<{
    modelValue: string;
    placeholder?: string;
    disabled?: boolean;
}>();

const emit = defineEmits<{
    (e: "update:modelValue", value: string): void;
}>();

const mostrarSenha = ref(false);

function atualizarValor(event: Event) {
    const target = event.target as HTMLInputElement;
    emit("update:modelValue", target.value);
}
</script>

<style scoped>

.password-input{
    position: relative;
    display: flex;
    align-items: center;
    width: 100%;
}

.password-input input{
    width: 100%;
    height: 60px;
    padding: 10px;
    padding-right: 55px;

    border: 1px solid #ccc;
    border-radius: 26px;

    font-size: 18px;
    box-sizing: border-box;
}

.toggle-password{
    position: absolute;
    right: 18px;

    display: flex;
    align-items: center;
    justify-content: center;

    border: none;
    background: transparent;

    cursor: pointer;
    color: #777;
}

.toggle-password:hover{
    color: #94335c;
}

</style>