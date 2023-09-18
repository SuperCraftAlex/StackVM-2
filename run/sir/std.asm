_add:
    sto add_addr_tmp

    add

    ldo add_addr_tmp
    ret

_sub:
    sto add_addr_tmp

    sub

    ldo add_addr_tmp
    ret

add_addr_tmp:
    empty 1