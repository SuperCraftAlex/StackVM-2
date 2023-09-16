package me.alex_s168.stackvm2.format.exception

class GlobalLabelAlreadyDefinedException(label: String):
    Exception("Global label $label already defined!")