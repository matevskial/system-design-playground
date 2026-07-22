<template>
  <Vueform>
    <ButtonElement :disabled="disabled" :buttonClass="buttonClass" name="Button" @click="$emit('click')">{{ text }}</ButtonElement>
  </Vueform>
</template>

<script>

export default {
  name: "Button",
  props: {
    text: {
      required: true,
      default: '',
    },
    type: {
      // possible values: 'primary', 'danger'
      required: false,
      default: 'primary'
    },
    disabled: {
      required: false,
      default: false
    }
  },
  computed: {
    buttonClass() {
      if (this.type === 'primary') {
        return 'vf-btn-primary';
      } else if (this.type === 'danger') {
        return 'vf-btn-danger';
      } else {
        return 'vf-btn-primary';
      }
    }
  }
}
</script>
